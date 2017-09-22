/*
DROP TABLE PASAJEROS;
DROP TABLE FACTURAS;
DROP TABLE HOTELES;
DROP TABLE HORARIOS;
DROP TABLE LOCALIDADES; 
DROP TABLE PAQUETES;
DROP TABLE LOCALIDADESPAQUETES;
DROP TABLE PASAJEROSPAQUETES;


DELETE FROM PASAJEROS;
DELETE FROM HOTELES;
DELETE FROM LOCALIDADES;
DELETE FROM HORARIOS;
*/

SELECT * FROM PASAJEROS;
SELECT * FROM HOTELES;
SELECT * FROM LOCALIDADES;
SELECT * FROM HORARIOS;


INSERT INTO PASAJEROS VALUES ('LEIA ORGANA','19500101','12345678','hansolonoexistis@email.com');
INSERT INTO PASAJEROS VALUES ('MAESTRO YODA','19300124','12345679','escribimepadawan@email.com');
INSERT INTO PASAJEROS VALUES ('DARTH VADER','19420713','12345680','soytupadre@email.com');
INSERT INTO PASAJEROS VALUES ('LUKE SKYWALKER','19710825','12345681','soyunjedimas@email.com');

INSERT INTO HOTELES VALUES ('HOTELES SHERATON','5700','5');
INSERT INTO HOTELES VALUES ('HOTELES HILTON','7800','5');
INSERT INTO HOTELES VALUES ('HOTELES ALL','1100','3');

INSERT INTO LOCALIDADES VALUES ('SANTA FE','3500');
INSERT INTO LOCALIDADES VALUES ('SAN JUAN','4500');
INSERT INTO LOCALIDADES VALUES ('CORDOBA','3000');
INSERT INTO LOCALIDADES VALUES ('MISIONES','5000');
INSERT INTO LOCALIDADES VALUES ('PUNTA DEL ESTE','5500');
INSERT INTO LOCALIDADES VALUES ('SANTIAGO','6000');
INSERT INTO LOCALIDADES VALUES ('QUITO','10300');
INSERT INTO LOCALIDADES VALUES ('MIAMI','15000');
INSERT INTO LOCALIDADES VALUES ('RIO DE JANIERO','7900');

INSERT INTO HORARIOS VALUES ('07:30','MANANA');
INSERT INTO HORARIOS VALUES ('09:30','MANANA');
INSERT INTO HORARIOS VALUES ('11:45','MANANA');
INSERT INTO HORARIOS VALUES ('14:30','TARDE');
INSERT INTO HORARIOS VALUES ('17:00','TARDE');
INSERT INTO HORARIOS VALUES ('19:45','TARDE');
INSERT INTO HORARIOS VALUES ('21:00','NOCHE');
INSERT INTO HORARIOS VALUES ('22:00','NOCHE');
INSERT INTO HORARIOS VALUES ('23:45','NOCHE');
