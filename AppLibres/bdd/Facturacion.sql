/*==============================================================*/
/* DBMS name:      PostgreSQL 9.x                               */
/* Created on:     23/1/2017 19:24:16                           */
/*==============================================================*/


/*==============================================================*/
/* Table: CLIENTE                                               */
/*==============================================================*/
create table CLIENTE (
   ID_CLIENTE           CHAR(10)             not null,
   CONTRASENA           VARCHAR(30)          not null,
   NOMBRE_CLIENTE       VARCHAR(50)          not null,
   EMAIL_CLIENTE        VARCHAR(30)          null,
   constraint PK_CLIENTE primary key (ID_CLIENTE)
);

/*==============================================================*/
/* Index: CLIENTE_PK                                            */
/*==============================================================*/
create unique index CLIENTE_PK on CLIENTE (
ID_CLIENTE
);

/*==============================================================*/
/* Table: ESTABLECIMIENTO                                       */
/*==============================================================*/
create table ESTABLECIMIENTO (
   ID_ESTABLECIMIENTO   CHAR(13)             not null,
   NOMBRE_ESTABLECIMIENTO VARCHAR(300)         not null,
   DIRECCION_ESTABLECIMIENTO VARCHAR(300)         null,
   TELEFONO_ESTABLECIMIENTO VARCHAR(10)          null,
   constraint PK_ESTABLECIMIENTO primary key (ID_ESTABLECIMIENTO)
);

/*==============================================================*/
/* Index: ESTABLECIMIENTO_PK                                    */
/*==============================================================*/
create unique index ESTABLECIMIENTO_PK on ESTABLECIMIENTO (
ID_ESTABLECIMIENTO
);

/*==============================================================*/
/* Table: FACTURA                                               */
/*==============================================================*/
create table FACTURA (
   ID_FACTURA           VARCHAR(20)          not null,
   ID_CLIENTE           CHAR(10)             not null,
   ID_ESTABLECIMIENTO   CHAR(13)             not null,
   TIPO_FACTURA         VARCHAR(10)          not null,
   FECHA_EMISION        DATE                 not null,
   ESTADO_FACTURA       VARCHAR(15)          null,
   AMBIENTE_FACTURA     VARCHAR(15)          null,
   TOTAL_SIN_IVA        MONEY                not null,
   IVA                  MONEY                not null,
   TOTAL_CON_IVA        MONEY                not null,
   constraint PK_FACTURA primary key (ID_FACTURA)
);

/*==============================================================*/
/* Index: FACTURA_PK                                            */
/*==============================================================*/
create unique index FACTURA_PK on FACTURA (
ID_FACTURA
);

/*==============================================================*/
/* Index: RELATIONSHIP_3_FK                                     */
/*==============================================================*/
create  index RELATIONSHIP_3_FK on FACTURA (
ID_ESTABLECIMIENTO
);

/*==============================================================*/
/* Index: RELATIONSHIP_4_FK                                     */
/*==============================================================*/
create  index RELATIONSHIP_4_FK on FACTURA (
ID_CLIENTE
);

/*==============================================================*/
/* Table: GASTOSANUALESPERSONALES                               */
/*==============================================================*/
create table GASTOSANUALESPERSONALES (
   ANIO_GASTOS          INT4                 not null,
   TOTAL_ALIMENTACION   MONEY                not null,
   TOTAL_SALUD          MONEY                not null,
   TOTAL_VIVIENDA       MONEY                not null,
   TOTAL_EDUCACION      MONEY                not null,
   TOTAL_VESTIMENTA     MONEY                not null,
   constraint PK_GASTOSANUALESPERSONALES primary key (ANIO_GASTOS)
);

/*==============================================================*/
/* Index: GASTOSANUALESPERSONALES_PK                            */
/*==============================================================*/
create unique index GASTOSANUALESPERSONALES_PK on GASTOSANUALESPERSONALES (
ANIO_GASTOS
);

/*==============================================================*/
/* Table: HISTORIAL_PAGOS_NEGOCIOS                              */
/*==============================================================*/
create table HISTORIAL_PAGOS_NEGOCIOS (
   ANIO_HISTORIAL_N     INT4                 not null,
   ID_CLIENTE           CHAR(10)             null,
   TOTAL_MERCADERIA     MONEY                null,
   TOTAL_ARRIENDO       MONEY                null,
   TOTAL_SERVICIOS      MONEY                null,
   TOTAL_SUELDOS        MONEY                null,
   TOTAL_MOVILIZACION   MONEY                null,
   TOTAL_VIATICOS       MONEY                null,
   TOTAL_CAPACITACION   MONEY                null,
   TOTAL_SUMINISTROS    MONEY                null,
   TOTAL_HERRAMIENTAS   MONEY                null,
   constraint PK_HISTORIAL_PAGOS_NEGOCIOS primary key (ANIO_HISTORIAL_N)
);

/*==============================================================*/
/* Index: HISTORIAL_PAGOS_NEGOCIOS_PK                           */
/*==============================================================*/
create unique index HISTORIAL_PAGOS_NEGOCIOS_PK on HISTORIAL_PAGOS_NEGOCIOS (
ANIO_HISTORIAL_N
);

/*==============================================================*/
/* Index: RELATIONSHIP_6_FK                                     */
/*==============================================================*/
create  index RELATIONSHIP_6_FK on HISTORIAL_PAGOS_NEGOCIOS (
ID_CLIENTE
);

/*==============================================================*/
/* Table: HISTORIAL_PAGOS_PERSONALES                            */
/*==============================================================*/
create table HISTORIAL_PAGOS_PERSONALES (
   ANIO_HISTORIAL_P     INT4                 not null,
   ID_CLIENTE           CHAR(10)             null,
   TOTAL_ALIMENTACION   MONEY                null,
   TOTAL_SALUD          MONEY                null,
   TOTAL_VIVIENDA       MONEY                null,
   TOTAL_EDUCACION      MONEY                null,
   TOTAL_VESTIMENTA     MONEY                null,
   TOTAL_OTROS          MONEY                null,
   constraint PK_HISTORIAL_PAGOS_PERSONALES primary key (ANIO_HISTORIAL_P)
);

/*==============================================================*/
/* Index: HISTORIAL_PAGOS_PERSONALES_PK                         */
/*==============================================================*/
create unique index HISTORIAL_PAGOS_PERSONALES_PK on HISTORIAL_PAGOS_PERSONALES (
ANIO_HISTORIAL_P
);

/*==============================================================*/
/* Index: RELATIONSHIP_5_FK                                     */
/*==============================================================*/
create  index RELATIONSHIP_5_FK on HISTORIAL_PAGOS_PERSONALES (
ID_CLIENTE
);

/*==============================================================*/
/* Table: TIPO_GASTO                                            */
/*==============================================================*/
create table TIPO_GASTO (
   ID_FACTURA           VARCHAR(20)          not null,
   TIPO                 VARCHAR(20)          not null,
   TOTAL                MONEY                not null
);

/*==============================================================*/
/* Index: RELATIONSHIP_1_FK                                     */
/*==============================================================*/
create  index RELATIONSHIP_1_FK on TIPO_GASTO (
ID_FACTURA
);

alter table FACTURA
   add constraint FK_FACTURA_RELATIONS_ESTABLEC foreign key (ID_ESTABLECIMIENTO)
      references ESTABLECIMIENTO (ID_ESTABLECIMIENTO)
      on delete restrict on update restrict;

alter table FACTURA
   add constraint FK_FACTURA_RELATIONS_CLIENTE foreign key (ID_CLIENTE)
      references CLIENTE (ID_CLIENTE)
      on delete restrict on update restrict;

alter table HISTORIAL_PAGOS_NEGOCIOS
   add constraint FK_HISTORIA_RELATIONS_CLIENTE foreign key (ID_CLIENTE)
      references CLIENTE (ID_CLIENTE)
      on delete restrict on update restrict;

alter table HISTORIAL_PAGOS_PERSONALES
   add constraint FK_HISTORIA_RELATIONS_CLIENTE foreign key (ID_CLIENTE)
      references CLIENTE (ID_CLIENTE)
      on delete restrict on update restrict;

alter table TIPO_GASTO
   add constraint FK_TIPO_GAS_RELATIONS_FACTURA foreign key (ID_FACTURA)
      references FACTURA (ID_FACTURA)
      on delete restrict on update restrict;


-- procedure para borrar clientes
 CREATE OR REPLACE FUNCTION borrarCliente (id varchar) RETURNS void AS $$
     BEGIN
         delete from tipo_gasto where id_factura in (select id_factura from factura where id_cliente=$1);
         delete from factura where id_cliente=$1;
         delete from historial_pagos_negocios where id_cliente=$1;
         delete from historial_pagos_personales where id_cliente=$1;
         delete from cliente where id_cliente=$1;
     END;
 $$ LANGUAGE plpgsql;

 -- Cargar límites de gastos personales
 INSERT INTO GASTOSANUALESPERSONALES VALUES (2016, 3630.25, 14521.00, 3630.25, 3630.25, 3630.25);
 INSERT INTO GASTOSANUALESPERSONALES VALUES (2017, 3669.25, 14677.00, 3669.25, 3669.25, 3669.25);
