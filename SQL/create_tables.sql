 ---------------------------------------------------------------
 --        Script Oracle.  
 ---------------------------------------------------------------


------------------------------------------------------------
-- Table: Client
------------------------------------------------------------
CREATE TABLE Client(
	id_client         NUMBER NOT NULL ,
	nom_client        VARCHAR2 (50) NOT NULL  ,
	prenom_client     VARCHAR2 (50) NOT NULL  ,
	date_nais_client  DATE  NOT NULL  ,
	tel_client        VARCHAR2 (50) NOT NULL  ,
	email_client      VARCHAR2 (50) NOT NULL  ,
	isDeleted_client  NUMBER(1) DEFAULT 0 CHECK (isDeleted_client IN (0, 1)),
	CONSTRAINT Client_PK PRIMARY KEY (id_client)
);

------------------------------------------------------------
-- Table: Specialiste
------------------------------------------------------------
CREATE TABLE Specialiste(
	id_spec         NUMBER NOT NULL ,
	nom_spec        VARCHAR2 (50) NOT NULL  ,
	prenom_spec     VARCHAR2 (50) NOT NULL  ,
	date_nais_spec  DATE  NOT NULL  ,
	tel_spec        VARCHAR2 (50) NOT NULL  ,
	email_spec      VARCHAR2 (50) NOT NULL  ,
	isDeleted_spec  NUMBER(1) DEFAULT 0 CHECK (isDeleted_spec IN (0, 1)),
	CONSTRAINT Specialiste_PK PRIMARY KEY (id_spec)
);

------------------------------------------------------------
-- Table: Competence
------------------------------------------------------------
CREATE TABLE Competence(
	id_comp   NUMBER NOT NULL ,
	nom_comp  VARCHAR2 (50) NOT NULL  ,
	isDeleted_competence NUMBER(1) DEFAULT 0 CHECK (isDeleted_competence IN (0, 1)),
	CONSTRAINT Competence_PK PRIMARY KEY (id_comp)
);

------------------------------------------------------------
-- Table: Lieu
------------------------------------------------------------
CREATE TABLE Lieu(
	id_lieu   NUMBER NOT NULL ,
	nom_lieu  VARCHAR2 (50) NOT NULL  ,
	isDeleted_lieu  NUMBER(1) DEFAULT 0 CHECK (isDeleted_lieu IN (0, 1)),
	CONSTRAINT Lieu_PK PRIMARY KEY (id_lieu)
);

------------------------------------------------------------
-- Table: Acte_med
------------------------------------------------------------
CREATE TABLE Acte_med(
	id_am      NUMBER NOT NULL ,
	REF_am     VARCHAR2 (50) NOT NULL  ,
	Date_deb   DATE  NOT NULL  ,
	Date_fin   DATE  NOT NULL  ,
	id_client  NUMBER(10,0)  NOT NULL  ,
	id_lieu    NUMBER(10,0)  NOT NULL  ,
	id_spec    NUMBER(10,0)  NOT NULL  ,
	isDeleted_am NUMBER(1) DEFAULT 0 CHECK (isDeleted_am IN (0, 1)),
	CONSTRAINT Acte_med_PK PRIMARY KEY (id_am)

	,CONSTRAINT Acte_med_Client_FK FOREIGN KEY (id_client) REFERENCES Client(id_client)
	,CONSTRAINT Acte_med_Lieu0_FK FOREIGN KEY (id_lieu) REFERENCES Lieu(id_lieu)
	,CONSTRAINT Acte_med_Specialiste1_FK FOREIGN KEY (id_spec) REFERENCES Specialiste(id_spec)
);

------------------------------------------------------------
-- Table: type_doc
------------------------------------------------------------
CREATE TABLE type_doc(
	id_td   NUMBER NOT NULL ,
	nom_td  VARCHAR2 (50) NOT NULL  ,
	isDeleted_td  NUMBER(1) DEFAULT 0 CHECK (isDeleted_td IN (0, 1)),
	CONSTRAINT type_doc_PK PRIMARY KEY (id_td)
);

------------------------------------------------------------
-- Table: Document
------------------------------------------------------------
CREATE TABLE Document(
	id_doc     NUMBER NOT NULL ,
	REF_doc    VARCHAR2 (50) NOT NULL  ,
	date_doc   DATE  NOT NULL  ,
	id_am      NUMBER(10,0)  NOT NULL  ,
	id_td      NUMBER(10,0)  NOT NULL  ,
	id_client  NUMBER(10,0)  NOT NULL  ,
	isDeleted_doc  NUMBER(1) DEFAULT 0 CHECK (isDeleted_doc IN (0, 1)),
	CONSTRAINT Document_PK PRIMARY KEY (id_doc)

	,CONSTRAINT Document_Acte_med_FK FOREIGN KEY (id_am) REFERENCES Acte_med(id_am)
	,CONSTRAINT Document_type_doc0_FK FOREIGN KEY (id_td) REFERENCES type_doc(id_td)
	,CONSTRAINT Document_Client1_FK FOREIGN KEY (id_client) REFERENCES Client(id_client)
);

------------------------------------------------------------
-- Table: Posseder
------------------------------------------------------------
CREATE TABLE Posseder(
	id_comp  NUMBER(10,0)  NOT NULL  ,
	id_spec  NUMBER(10,0)  NOT NULL  ,
	CONSTRAINT Posseder_PK PRIMARY KEY (id_comp,id_spec)

	,CONSTRAINT Posseder_Competence_FK FOREIGN KEY (id_comp) REFERENCES Competence(id_comp)
	,CONSTRAINT Posseder_Specialiste0_FK FOREIGN KEY (id_spec) REFERENCES Specialiste(id_spec)
);

------------------------------------------------------------
-- Table: Necessiter
------------------------------------------------------------
CREATE TABLE Necessiter(
	id_comp  NUMBER(10,0)  NOT NULL  ,
	id_am    NUMBER(10,0)  NOT NULL  ,
	CONSTRAINT Necessiter_PK PRIMARY KEY (id_comp,id_am)

	,CONSTRAINT Necessiter_Competence_FK FOREIGN KEY (id_comp) REFERENCES Competence(id_comp)
	,CONSTRAINT Necessiter_Acte_med0_FK FOREIGN KEY (id_am) REFERENCES Acte_med(id_am)
);

------------------------------------------------------------
-- Table: Log
------------------------------------------------------------
CREATE TABLE debug_log (
  log_id         NUMBER NOT NULL,
  log_date       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  procedure_name VARCHAR2(100),
  variable_name  VARCHAR2(100),
  variable_value VARCHAR2(4000)
);



CREATE SEQUENCE Seq_Client_id_client START WITH 1 INCREMENT BY 1 NOCYCLE;
CREATE SEQUENCE Seq_Specialiste_id_spec START WITH 1 INCREMENT BY 1 NOCYCLE;
CREATE SEQUENCE Seq_Competence_id_comp START WITH 1 INCREMENT BY 1 NOCYCLE;
CREATE SEQUENCE Seq_Lieu_id_lieu START WITH 1 INCREMENT BY 1 NOCYCLE;
CREATE SEQUENCE Seq_Acte_med_id_am START WITH 1 INCREMENT BY 1 NOCYCLE;
CREATE SEQUENCE Seq_type_doc_id_td START WITH 1 INCREMENT BY 1 NOCYCLE;
CREATE SEQUENCE Seq_Document_id_doc START WITH 1 INCREMENT BY 1 NOCYCLE;
CREATE SEQUENCE Seq_debug_log START WITH 1 INCREMENT BY 1 NOCYCLE;


CREATE OR REPLACE TRIGGER Client_id_client
	BEFORE INSERT ON Client 
  FOR EACH ROW 
	WHEN (NEW.id_client IS NULL) 
	BEGIN
		 select Seq_Client_id_client.NEXTVAL INTO :NEW.id_client from DUAL; 
	END;
	/
CREATE OR REPLACE TRIGGER Specialiste_id_spec
	BEFORE INSERT ON Specialiste 
  FOR EACH ROW 
	WHEN (NEW.id_spec IS NULL) 
	BEGIN
		 select Seq_Specialiste_id_spec.NEXTVAL INTO :NEW.id_spec from DUAL; 
	END;
	/
CREATE OR REPLACE TRIGGER Competence_id_comp
	BEFORE INSERT ON Competence 
  FOR EACH ROW 
	WHEN (NEW.id_comp IS NULL) 
	BEGIN
		 select Seq_Competence_id_comp.NEXTVAL INTO :NEW.id_comp from DUAL; 
	END;
	/
CREATE OR REPLACE TRIGGER Lieu_id_lieu
	BEFORE INSERT ON Lieu 
  FOR EACH ROW 
	WHEN (NEW.id_lieu IS NULL) 
	BEGIN
		 select Seq_Lieu_id_lieu.NEXTVAL INTO :NEW.id_lieu from DUAL; 
	END;
	/
CREATE OR REPLACE TRIGGER Acte_med_id_am
	BEFORE INSERT ON Acte_med 
  FOR EACH ROW 
	WHEN (NEW.id_am IS NULL) 
	BEGIN
		 select Seq_Acte_med_id_am.NEXTVAL INTO :NEW.id_am from DUAL; 
	END;
	/
CREATE OR REPLACE TRIGGER type_doc_id_td
	BEFORE INSERT ON type_doc 
  FOR EACH ROW 
	WHEN (NEW.id_td IS NULL) 
	BEGIN
		 select Seq_type_doc_id_td.NEXTVAL INTO :NEW.id_td from DUAL; 
	END;
	/
CREATE OR REPLACE TRIGGER Document_id_doc
	BEFORE INSERT ON Document 
  FOR EACH ROW 
	WHEN (NEW.id_doc IS NULL) 
	BEGIN
		 select Seq_Document_id_doc.NEXTVAL INTO :NEW.id_doc from DUAL; 
	END;
	/
CREATE OR REPLACE TRIGGER debug_log
	BEFORE INSERT ON debug_log 
  FOR EACH ROW 
	WHEN (NEW.log_id IS NULL) 
	BEGIN
		select Seq_debug_log.NEXTVAL INTO :NEW.log_id from DUAL; 
	END;
	/
