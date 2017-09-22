ALTER PROCEDURE [dbo].[sp_ingresar_nuevo_paquete]
	@importe decimal,
	@tipo_factura char,
    @tiene_seguro bit,
	@quiere_visita_guiada bit,
	@quiere_abono_transporte_local bit,
	@es_pension_completa bit,
	@hotel_id int,
	@fecha_hora_salida datetime,
	@cantidad_dias int,
	@localidades varchar(MAX),
	@pasajeros varchar(MAX)

AS

	DECLARE @paqueteId int
	DECLARE @facturaId int
	DECLARE @lstDato varchar(MAX)
	DECLARE @lnuPosComa int

		--FACTURAS

		insert into Facturas(fecha, importe, tipo)
			values(getDate(), @importe, @tipo_factura)
		
		set @facturaId = @@IDENTITY

		PRINT 'Factura: ' + cast(@facturaId as varchar)

		--PAQUETES

		insert into Paquetes(cantidad_dias, es_pension_completa,factura_id,
			fecha_hora_salida,hotel_id,importe,quiere_abono_transporte_local,
			quiere_visita_guiada, tiene_seguro)
		values(
			@cantidad_dias,
			@es_pension_completa,
			@facturaId,
			@fecha_hora_salida,
			@hotel_id,
			@importe,
			@quiere_abono_transporte_local,
			@quiere_visita_guiada,
			@tiene_seguro
		)

		set @paqueteId = @@IDENTITY

		PRINT 'Paquete: ' + cast(@paqueteId as varchar)

		--LOCALIDADES_PAQUETES

		WHILE  LEN(@localidades)> 0
		BEGIN
			SET @lnuPosComa = CHARINDEX(',', @localidades )
			IF ( @lnuPosComa=0 )
				BEGIN
					SET @lstDato = @localidades
					SET @localidades = ''
				END
			ELSE
				BEGIN
					SET @lstDato = Substring( @localidades , 1  , @lnuPosComa-1)
					SET @localidades = Substring( @localidades , @lnuPosComa + 1 , LEN(@localidades))
				END
	
			PRINT 'Localidad: ' + ltrim(rtrim(@lstDato ))

			insert into LocalidadesPaquetes(localidad_id,paquete_id)
				select l.id, @paqueteId
				from Localidades l
				where l.localidad = ltrim(rtrim(@lstDato ))
		END

		--PASAJEROS_PAQUETES

	WHILE  LEN(@pasajeros)> 0
		BEGIN
			SET @lnuPosComa = CHARINDEX(',', @pasajeros )
			IF ( @lnuPosComa=0 )
				BEGIN
					SET @lstDato = @pasajeros
					SET @pasajeros = ''
				END
			ELSE
				BEGIN
					SET @lstDato = Substring( @pasajeros , 1  , @lnuPosComa-1)
					SET @pasajeros = Substring( @pasajeros , @lnuPosComa + 1 , LEN(@pasajeros))
				END
	
			PRINT 'Pasajero: ' + ltrim(rtrim(@lstDato))

			insert into PasajerosPaquetes(paquete_id, pasajero_id)
				select @paqueteId, p.id
				from Pasajeros p
				where p.dni = cast(ltrim(rtrim(@lstDato)) as int) 
		END