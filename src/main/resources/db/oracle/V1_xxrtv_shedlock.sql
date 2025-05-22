--
-- Opprettes som apps bruker i OeBS
--
CREATE TABLE "XXRTV"."XXRTV_SHEDLOCK"
(	"NAME" VARCHAR2(64 BYTE) NOT NULL ENABLE,
     "LOCK_UNTIL" TIMESTAMP (3) NOT NULL ENABLE,
     "LOCKED_AT" TIMESTAMP (3) NOT NULL ENABLE,
     "LOCKED_BY" VARCHAR2(255 BYTE) NOT NULL ENABLE,
     PRIMARY KEY ("NAME"));

create synonym xxrtv_shedlock for xxrtv.xxrtv_shedlock;