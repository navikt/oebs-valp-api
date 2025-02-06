-------------------------------------------------------------------------------
-- Skjema            : XXRTV
-- Tabell            : XXRTV_VALP_LOGG
-- Kildefil          : V1__xxrtv_valp_logg.sql
-- Beskrivelse       : Loggtabell for API-kall.
-------------------------------------------------------------------------------
CREATE TABLE xxrtv.xxrtv_valp_logg( kall_logg_id        NUMBER        NOT NULL
                      , korrelasjon_id      VARCHAR2(50)  NOT NULL
                      , tidspunkt           TIMESTAMP(9)  NOT NULL
                      , type                VARCHAR2(10)  NOT NULL
                      , kall_retning        VARCHAR2(10)  NOT NULL
                      , method              VARCHAR2(10)
                      , operation           VARCHAR2(100) NOT NULL
                      , status              NUMBER
                      , kalltid             NUMBER        NOT NULL
                      , request             CLOB
                      , response            CLOB
                      , logginfo            CLOB
                      , CONSTRAINT xxrtv_valp_kalo_pk PRIMARY KEY(kall_logg_id))
PARTITION BY RANGE(tidspunkt)
INTERVAL(NUMTOYMINTERVAL(1, 'MONTH'))
( PARTITION kall_logg_data_p1 VALUES LESS THAN( DATE '2025-02-01'));

CREATE synonym xxrtv_valp_logg FOR xxrtv.xxrtv_valp_logg;

CREATE INDEX xxrtv_valp_kalo_U3 ON xxrtv_valp_logg
(status) LOCAL;

-- Legg til sekvens og trigger
CREATE  SEQUENCE xxrtv_valp_seq
START WITH 1
INCREMENT BY 1;

CREATE OR REPLACE TRIGGER xxrtv_valp_kalo_bir
  BEFORE INSERT ON xxrtv_valp_logg
  FOR EACH ROW
BEGIN
  SELECT xxrtv_valp_seq.nextval
  INTO   :new.kall_logg_id
  FROM   DUAL;
END;

ALTER TABLE xxrtv_valp_logg
    ADD (CONSTRAINT xxrtv_type_ck1 CHECK (type IN ('PLSQL', 'REST')));

ALTER TABLE xxrtv_valp_logg
    ADD (CONSTRAINT xxrtv_kall_retning_ck1 CHECK (kall_retning IN ('INN','UT')));

CREATE INDEX xxrtv_valp_kalo_U1 ON xxrtv_valp_logg
    (operation, kall_retning);

CREATE INDEX xxrtv_valp_kalo_U2 ON xxrtv_valp_logg
    (korrelasjon_id);