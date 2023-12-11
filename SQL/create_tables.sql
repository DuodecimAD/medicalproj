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
	id_specialiste         NUMBER NOT NULL ,
	nom_specialiste        VARCHAR2 (50) NOT NULL  ,
	prenom_specialiste     VARCHAR2 (50) NOT NULL  ,
	date_nais_specialiste  DATE  NOT NULL  ,
	tel_specialiste        VARCHAR2 (50) NOT NULL  ,
	email_specialiste      VARCHAR2 (50) NOT NULL  ,
	isDeleted_specialiste  NUMBER(1) DEFAULT 0 CHECK (isDeleted_specialiste IN (0, 1)),
	CONSTRAINT Specialiste_PK PRIMARY KEY (id_specialiste)
);

------------------------------------------------------------
-- Table: Competence
------------------------------------------------------------
CREATE TABLE Competence(
	id_competence   NUMBER NOT NULL ,
	nom_competence  VARCHAR2 (50) NOT NULL  ,
	isDeleted_competence NUMBER(1) DEFAULT 0 CHECK (isDeleted_competence IN (0, 1)),
	CONSTRAINT Competence_PK PRIMARY KEY (id_competence)
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
	id_acte_med        NUMBER NOT NULL ,
	REF_acte_med       VARCHAR2 (50) NOT NULL  ,
	Date_debut         DATE  NOT NULL  ,
	Date_fin           DATE  NOT NULL  ,
	id_client          NUMBER(10,0)  NOT NULL  ,
	id_lieu            NUMBER(10,0)  NOT NULL  ,
	id_specialiste     NUMBER(10,0)  NOT NULL  ,
	isDeleted_acte_med NUMBER(1) DEFAULT 0 CHECK (isDeleted_acte_med IN (0, 1)),
	CONSTRAINT Acte_med_PK PRIMARY KEY (id_acte_med)

	,CONSTRAINT Acte_med_Client_FK FOREIGN KEY (id_client) REFERENCES Client(id_client)
	,CONSTRAINT Acte_med_Lieu0_FK FOREIGN KEY (id_lieu) REFERENCES Lieu(id_lieu)
	,CONSTRAINT Acte_med_Specialiste1_FK FOREIGN KEY (id_specialiste) REFERENCES Specialiste(id_specialiste)
);

------------------------------------------------------------
-- Table: type_doc
------------------------------------------------------------
CREATE TABLE Type_doc(
	id_type_doc   NUMBER NOT NULL ,
	nom_type_doc  VARCHAR2 (50) NOT NULL  ,
	isDeleted_type_doc  NUMBER(1) DEFAULT 0 CHECK (isDeleted_type_doc IN (0, 1)),
	CONSTRAINT type_doc_PK PRIMARY KEY (id_type_doc)
);

------------------------------------------------------------
-- Table: Document
------------------------------------------------------------
CREATE TABLE Document(
	id_document     NUMBER NOT NULL ,
	REF_document    VARCHAR2 (50) NOT NULL  ,
	date_document   DATE  NOT NULL  ,
	id_acte_med      NUMBER(10,0)  NOT NULL  ,
	id_type_doc      NUMBER(10,0)  NOT NULL  ,
	id_client  NUMBER(10,0)  NOT NULL  ,
	isDeleted_document  NUMBER(1) DEFAULT 0 CHECK (isDeleted_document IN (0, 1)),
	CONSTRAINT Document_PK PRIMARY KEY (id_document)

	,CONSTRAINT Document_Acte_med_FK FOREIGN KEY (id_acte_med) REFERENCES Acte_med(id_acte_med)
	,CONSTRAINT Document_type_doc0_FK FOREIGN KEY (id_type_doc) REFERENCES type_doc(id_type_doc)
	,CONSTRAINT Document_Client1_FK FOREIGN KEY (id_client) REFERENCES Client(id_client)
);

------------------------------------------------------------
-- Table: Posseder
------------------------------------------------------------
CREATE TABLE Posseder(
	id_competence  NUMBER(10,0)  NOT NULL  ,
	id_specialiste  NUMBER(10,0)  NOT NULL  ,
	CONSTRAINT Posseder_PK PRIMARY KEY (id_competence,id_specialiste)

	,CONSTRAINT Posseder_Competence_FK FOREIGN KEY (id_competence) REFERENCES Competence(id_competence)
	,CONSTRAINT Posseder_Specialiste0_FK FOREIGN KEY (id_specialiste) REFERENCES Specialiste(id_specialiste)
);

------------------------------------------------------------
-- Table: Necessiter
------------------------------------------------------------
CREATE TABLE Necessiter(
	id_competence  NUMBER(10,0)  NOT NULL  ,
	id_acte_med    NUMBER(10,0)  NOT NULL  ,
	CONSTRAINT Necessiter_PK PRIMARY KEY (id_competence,id_acte_med)

	,CONSTRAINT Necessiter_Competence_FK FOREIGN KEY (id_competence) REFERENCES Competence(id_competence)
	,CONSTRAINT Necessiter_Acte_med0_FK FOREIGN KEY (id_acte_med) REFERENCES Acte_med(id_acte_med)
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
CREATE SEQUENCE Seq_Specialiste_id_specialiste START WITH 1 INCREMENT BY 1 NOCYCLE;
CREATE SEQUENCE Seq_Competence_id_competence START WITH 1 INCREMENT BY 1 NOCYCLE;
CREATE SEQUENCE Seq_Lieu_id_lieu START WITH 1 INCREMENT BY 1 NOCYCLE;
CREATE SEQUENCE Seq_Acte_med_id_acte_med START WITH 1 INCREMENT BY 1 NOCYCLE;
CREATE SEQUENCE Seq_Type_doc_id_type_doc START WITH 1 INCREMENT BY 1 NOCYCLE;
CREATE SEQUENCE Seq_Document_id_document START WITH 1 INCREMENT BY 1 NOCYCLE;
CREATE SEQUENCE Seq_debug_log START WITH 1 INCREMENT BY 1 NOCYCLE;


CREATE OR REPLACE TRIGGER Client_id_client
	BEFORE INSERT ON Client 
  FOR EACH ROW 
	WHEN (NEW.id_client IS NULL) 
	BEGIN
		 select Seq_Client_id_client.NEXTVAL INTO :NEW.id_client from DUAL; 
	END;
	/
CREATE OR REPLACE TRIGGER Specialiste_id_specialiste
	BEFORE INSERT ON Specialiste 
  FOR EACH ROW 
	WHEN (NEW.id_specialiste IS NULL) 
	BEGIN
		 select Seq_Specialiste_id_specialiste.NEXTVAL INTO :NEW.id_specialiste from DUAL; 
	END;
	/
CREATE OR REPLACE TRIGGER Competence_id_competence
	BEFORE INSERT ON Competence 
  FOR EACH ROW 
	WHEN (NEW.id_competence IS NULL) 
	BEGIN
		 select Seq_Competence_id_competence.NEXTVAL INTO :NEW.id_competence from DUAL; 
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
CREATE OR REPLACE TRIGGER Acte_med_id_acte_med
	BEFORE INSERT ON Acte_med 
  FOR EACH ROW 
	WHEN (NEW.id_acte_med IS NULL) 
	BEGIN
		 select Seq_Acte_med_id_acte_med.NEXTVAL INTO :NEW.id_acte_med from DUAL; 
	END;
	/
CREATE OR REPLACE TRIGGER Type_doc_id_type_doc
	BEFORE INSERT ON Type_doc 
  FOR EACH ROW 
	WHEN (NEW.id_type_doc IS NULL) 
	BEGIN
		 select Seq_type_doc_id_type_doc.NEXTVAL INTO :NEW.id_type_doc from DUAL; 
	END;
	/
CREATE OR REPLACE TRIGGER Document_id_document
	BEFORE INSERT ON Document 
  FOR EACH ROW 
	WHEN (NEW.id_document IS NULL) 
	BEGIN
		 select Seq_Document_id_document.NEXTVAL INTO :NEW.id_document from DUAL; 
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
