create or replace PROCEDURE CheckIfExists (
    p_tableName    IN VARCHAR2,
    p_column       IN VARCHAR2,
    p_value        IN VARCHAR2
)
IS
    v_tableName VARCHAR2(255) := p_tableName;
    v_column    VARCHAR2(255) := p_column;
    v_value     VARCHAR2(255) := p_value;
    sql_stmt    VARCHAR2(1000);
    RecordExists INT := 0;
BEGIN

    -- Log
    INSERT INTO debug_log (procedure_name, variable_name, variable_value)
    VALUES ('CheckIfExists', 'Entering Procedure', 'beginning CheckIf ' || v_value || ' Exists in ' || v_column);
    INSERT INTO debug_log (procedure_name, variable_name, variable_value)
    VALUES ('CheckIfExists', 'v_tableName', v_tableName );
    INSERT INTO debug_log (procedure_name, variable_name, variable_value)
    VALUES ('CheckIfExists', 'v_column', v_column);
    INSERT INTO debug_log (procedure_name, variable_name, variable_value)
    VALUES ('CheckIfExists', 'v_value', v_value);
    COMMIT;

    -- Construct the dynamic SQL statement
    sql_stmt := 'SELECT COUNT(*) FROM ' || v_tableName ||
                ' WHERE ' || v_column || ' = :1';

    -- Execute the dynamic SQL statement
    EXECUTE IMMEDIATE sql_stmt INTO RecordExists USING v_value;
    
      -- Log variable values into the debug table
    INSERT INTO debug_log (procedure_name, variable_name, variable_value)
    VALUES ('CheckIfExists', 'p_RecordExists', RecordExists);
    COMMIT;
    
    -- If the record doesn't exist, perform the insert
    IF RecordExists = 1 THEN
    -- Log
    INSERT INTO debug_log (procedure_name, variable_name, variable_value)
    VALUES ('CheckIfExists', 'error', v_value || ' exist : closing');
    COMMIT;
    
        -- Record already exists, raise an exception
        RAISE_APPLICATION_ERROR(-20001, v_column || ' : ' || v_value || ' already exists.');
    END IF;
    
    -- Log
    INSERT INTO debug_log (procedure_name, variable_name, variable_value)
    VALUES ('CheckIfExists', 'closing procedure', v_value || ' dont exist : can now do the insert');
    COMMIT;
    
END;
/

create or replace PROCEDURE InsertRecord (
    p_tableName IN VARCHAR2,
    p_columns   IN VARCHAR2,
    p_value1    IN VARCHAR2,
    p_value2    IN VARCHAR2,
    p_value3    IN DATE,
    p_value4    IN VARCHAR2,
    p_value5    IN VARCHAR2
)
IS
    v_tableName VARCHAR2(255)   := p_tableName;
    v_columns   VARCHAR2(255)   := p_columns;
    v_value1    VARCHAR2(255)   := p_value1;
    v_value2    VARCHAR2(255)   := p_value2;
    v_value3    DATE            := p_value3;
    v_value4    VARCHAR2(255)   := p_value4;
    v_value5    VARCHAR2(255)   := p_value5;
    sql_stmt VARCHAR2(1000);
BEGIN
    -- Logging
    INSERT INTO debug_log (procedure_name, variable_name, variable_value)
    VALUES ('InsertRecord', 'InsertRecord Procedure', 'insert started');
    COMMIT;
    
    sql_stmt := 'INSERT INTO ' || v_tableName || ' (' || v_columns || ') ' ||
                'VALUES (:1, :2, :3, :4, :5)';

    EXECUTE IMMEDIATE sql_stmt USING v_value1, v_value2, v_value3, v_value4, v_value5;
    
    -- Logging
    INSERT INTO debug_log (procedure_name, variable_name, variable_value)
    VALUES ('InsertRecord', 'InsertRecord Procedure', 'insert finished');
    COMMIT;
END;
/
create or replace PROCEDURE InsertIfNotExists (
    p_tableName     IN VARCHAR2,
    p_columns       IN VARCHAR2,
    p_values        IN VARCHAR2
)
IS 
    recordExists    INT;
    tableName       VARCHAR2(255)   := p_tableName;
    inputColumns    VARCHAR2(255)   := p_columns;
    inputValues     VARCHAR2(255)   := p_values;
    separator       VARCHAR2(1)     := ',';
    startPos        INT             := 1;
    endPos          INT;
    countPos        INT;
    c_values    SYS.ODCIVARCHAR2LIST := SYS.ODCIVARCHAR2LIST();
    c_result    VARCHAR2(100);
    c_value1    VARCHAR2(100);
    c_value2    VARCHAR2(100);
    c_value3    VARCHAR2(100);
    c_value4    VARCHAR2(100);
    c_value5    VARCHAR2(100);
    c_value6    VARCHAR2(100);
    c_value7    VARCHAR2(100);
    c_value8    VARCHAR2(100);
    c_value9    VARCHAR2(100);
    v_result    VARCHAR2(100);
    s_values    SYS.ODCIVARCHAR2LIST := SYS.ODCIVARCHAR2LIST();
    s_value1    VARCHAR2(100);
    s_value2    VARCHAR2(100);
    s_value3    VARCHAR2(100);
    s_value4    VARCHAR2(100);
    s_value5    VARCHAR2(100);
    s_value6    VARCHAR2(100);
    s_value7    VARCHAR2(100);
    s_value8    VARCHAR2(100);
    s_value9    VARCHAR2(100);
    i_value0    INT;
    i_value1    INT;
    i_value2    INT;
    i_value3    INT;
    i_value4    INT;
    d_value0    DATE;
    d_value1    DATE;
    d_value2    DATE;
    d_value3    DATE;
    d_value4    DATE;
    sql_stmt    VARCHAR2(1000);
BEGIN

    -- Log
    INSERT INTO debug_log (procedure_name, variable_name, variable_value)
    VALUES ('InsertIfNotExists', 'Entering Procedure', 'start ');
    INSERT INTO debug_log (procedure_name, variable_name, variable_value)
    VALUES ('InsertIfNotExists', 'tablename', tablename);
    INSERT INTO debug_log (procedure_name, variable_name, variable_value)
    VALUES ('InsertIfNotExists', 'inputColumns', inputColumns);
    INSERT INTO debug_log (procedure_name, variable_name, variable_value)
    VALUES ('InsertIfNotExists', 'inputValues', inputValues);
    COMMIT; 

/*
    countPos := 1;

    --IF tableName = 'CLIENT' THEN
    WHILE startPos <= LENGTH(inputColumns) LOOP
        endPos := INSTR(inputColumns, separator, startPos);

        IF endPos = 0 THEN
            endPos := LENGTH(inputColumns) + 1;
        END IF;

        IF countPos = 1 THEN
            c_value1 := TRIM(SUBSTR(inputColumns, startPos, endPos - startPos));
        ELSIF countPos = 2 THEN
            c_value2 := TRIM(SUBSTR(inputColumns, startPos, endPos - startPos));
        ELSIF countPos = 3 THEN
            c_value3 := TRIM(SUBSTR(inputColumns, startPos, endPos - startPos));
        ELSIF countPos = 4 THEN
            c_value4 := TRIM(SUBSTR(inputColumns, startPos, endPos - startPos));
        ELSIF countPos = 5 THEN
            c_value5 := TRIM(SUBSTR(inputColumns, startPos, endPos - startPos));
        END IF;

        startPos := endPos + 1;
        countPos := countPos + 1;
    END LOOP;
    
    -- Log
    INSERT INTO debug_log (procedure_name, variable_name, variable_value)
    VALUES ('InsertIfNotExists', 'c_value1', c_value1);
    INSERT INTO debug_log (procedure_name, variable_name, variable_value)
    VALUES ('InsertIfNotExists', 'c_value2', c_value2);
    INSERT INTO debug_log (procedure_name, variable_name, variable_value)
    VALUES ('InsertIfNotExists', 'c_value3', c_value3);
    INSERT INTO debug_log (procedure_name, variable_name, variable_value)
    VALUES ('InsertIfNotExists', 'c_value4', c_value4);
    INSERT INTO debug_log (procedure_name, variable_name, variable_value)
    VALUES ('InsertIfNotExists', 'c_value5', c_value5);
*/

/*
    startPos := 1;
    endPos := 0;
    countPos := 1;

    WHILE startPos <= LENGTH(inputValues) LOOP
        endPos := INSTR(inputValues, separator, startPos);

        IF endPos = 0 THEN
            endPos := LENGTH(inputValues) + 1;
        END IF;

        IF countPos = 1 THEN
            s_value1 := TRIM(SUBSTR(inputValues, startPos, endPos - startPos));
        ELSIF countPos = 2 THEN
            s_value2 := TRIM(SUBSTR(inputValues, startPos, endPos - startPos));
        ELSIF countPos = 3 THEN
            s_value3 := TRIM(SUBSTR(inputValues, startPos, endPos - startPos));
        ELSIF countPos = 4 THEN
            s_value4 := TRIM(SUBSTR(inputValues, startPos, endPos - startPos));
        ELSIF countPos = 5 THEN
            s_value5 := TRIM(SUBSTR(inputValues, startPos, endPos - startPos));
        END IF;

        startPos := endPos + 1;
        countPos := countPos + 1;
    END LOOP;

    -- Log
    INSERT INTO debug_log (procedure_name, variable_name, variable_value)
    VALUES ('InsertIfNotExists', 's_value1', s_value1);
    INSERT INTO debug_log (procedure_name, variable_name, variable_value)
    VALUES ('InsertIfNotExists', 's_value2', s_value2);
    INSERT INTO debug_log (procedure_name, variable_name, variable_value)
    VALUES ('InsertIfNotExists', 's_value3', s_value3);
    INSERT INTO debug_log (procedure_name, variable_name, variable_value)
    VALUES ('InsertIfNotExists', 's_value4', s_value4);
    INSERT INTO debug_log (procedure_name, variable_name, variable_value)
    VALUES ('InsertIfNotExists', 's_value5', s_value5);
    COMMIT;
*/

    -- Split the columns into an array
    SELECT TRIM(BOTH ' ' FROM REGEXP_SUBSTR(inputColumns, '[^' || separator || ']+', 1, LEVEL))
    BULK COLLECT INTO c_values
    FROM dual
    CONNECT BY REGEXP_SUBSTR(inputColumns, '[^' || separator || ']+', 1, LEVEL) IS NOT NULL;


    INSERT INTO debug_log (procedure_name, variable_name, variable_value)
    VALUES ('InsertIfNotExists', 'column 1 in array', c_values(1));
    COMMIT; 
    INSERT INTO debug_log (procedure_name, variable_name, variable_value)
    VALUES ('InsertIfNotExists', 'column 2 in array', c_values(2));
    COMMIT;
   
    
    
    -- Split the values into an array
    SELECT TRIM(BOTH ' ' FROM REGEXP_SUBSTR(inputValues, '[^' || separator || ']+', 1, LEVEL))
    BULK COLLECT INTO s_values
    FROM dual
    CONNECT BY REGEXP_SUBSTR(inputValues, '[^' || separator || ']+', 1, LEVEL) IS NOT NULL;


    INSERT INTO debug_log (procedure_name, variable_name, variable_value)
    VALUES ('InsertIfNotExists', 'value 1 in array', s_values(1));
    COMMIT; 
    INSERT INTO debug_log (procedure_name, variable_name, variable_value)
    VALUES ('InsertIfNotExists', 'value 2 in array', s_values(2));
    COMMIT;

    
    IF tableName = 'CLIENT' OR tableName = 'SPECIALISTE' THEN
    
        -- Check if the record exists
        CheckIfExists(tableName, c_values(4), s_values(4));
        -- Check if the record exists
        CheckIfExists(tableName, c_values(5), s_values(5));
        
        -- Convert the string to a DATE
        d_value1 := TO_DATE(s_values(3), 'YYYY-MM-DD'); 
    
        -- Log
        INSERT INTO debug_log (procedure_name, variable_name, variable_value)
        VALUES ('InsertIfNotExists', 'd_value1', d_value1);
        COMMIT;
        
        sql_stmt := 'INSERT INTO ' || tableName || ' (' || inputColumns || ') ' ||
                'VALUES (:1, :2, :3, :4, :5)';

        EXECUTE IMMEDIATE sql_stmt USING s_values(1), s_values(2), d_value1, s_values(4), s_values(5);
        
        INSERT INTO debug_log (procedure_name, variable_name, variable_value)
        VALUES ('InsertIfNotExists', 'ACTE_MED insert : table name : ', tableName);
        COMMIT;
        INSERT INTO debug_log (procedure_name, variable_name, variable_value)
        VALUES ('InsertIfNotExists', 'ACTE_MED insert : columns : ', inputColumns);
        COMMIT;
        INSERT INTO debug_log (procedure_name, variable_name, variable_value)
        VALUES ('InsertIfNotExists', 'ACTE_MED insert : values : ', s_values(1) || ',' || s_values(2) || ',' || d_value1 || ',' || s_values(4) || ',' || s_values(5));
        COMMIT;
        
        -- Record does not exist, proceed with the insert
        --InsertRecord(tableName, inputColumns, s_values(1), s_values(2), d_value1, s_values(4), s_values(5));
        
    ELSIF tableName = 'ACTE_MED' THEN
        
        -- Check if the record exists
        CheckIfExists(tableName, c_values(1), s_values(1));
        
        -- Convert the string to a DATE
        d_value1 := TO_DATE(s_values(2), 'YYYY-MM-DD');
        d_value2 := TO_DATE(s_values(3), 'YYYY-MM-DD'); 
    
        -- Log
        INSERT INTO debug_log (procedure_name, variable_name, variable_value)
        VALUES ('InsertIfNotExists', 'ACTE_MED d_value1', d_value1);
        COMMIT;
        INSERT INTO debug_log (procedure_name, variable_name, variable_value)
        VALUES ('InsertIfNotExists', 'ACTE_MED d_value2', d_value2);
        COMMIT;
        
        INSERT INTO debug_log (procedure_name, variable_name, variable_value)
        VALUES ('InsertIfNotExists', 'ACTE_MED insert : table name : ', tableName);
        COMMIT;
        INSERT INTO debug_log (procedure_name, variable_name, variable_value)
        VALUES ('InsertIfNotExists', 'ACTE_MED insert : columns : ', inputColumns);
        COMMIT;
        INSERT INTO debug_log (procedure_name, variable_name, variable_value)
        VALUES ('InsertIfNotExists', 'ACTE_MED insert : values : ', s_values(1) || ',' || d_value1 || ',' || d_value2 || ',' || s_values(4) || ',' || s_values(5) || ',' || s_values(6));
        COMMIT;
        
        sql_stmt := 'INSERT INTO ' || tableName || ' (' || inputColumns || ') ' ||
                'VALUES (:1, :2, :3, :4, :5, :6)';

        EXECUTE IMMEDIATE sql_stmt USING s_values(1), d_value1, d_value2, s_values(4), s_values(5), s_values(6);
        
    ELSIF tableName = 'Necessiter' THEN
        
        INSERT INTO debug_log (procedure_name, variable_name, variable_value)
        VALUES ('InsertIfNotExists', 'Necessiter insert : table name : ', tableName);
        COMMIT;
        INSERT INTO debug_log (procedure_name, variable_name, variable_value)
        VALUES ('InsertIfNotExists', 'Necessiter insert : columns : ', inputColumns);
        COMMIT;
        INSERT INTO debug_log (procedure_name, variable_name, variable_value)
        VALUES ('InsertIfNotExists', 'Necessiter insert : values : ', s_values(1) || ',' || s_values(2));
        COMMIT;
        
         sql_stmt := 'INSERT INTO ' || tableName || ' (' || inputColumns || ') ' ||
                'VALUES (:1, :2)';

        EXECUTE IMMEDIATE sql_stmt USING s_values(1), s_values(2);
        
        -- Record does not exist, proceed with the insert
        --InsertRecord(tableName, inputColumns, s_values(1), d_value1, d_value2, s_values(4), s_values(5), s_values(6));
       

    END IF;
    

    -- Log
    INSERT INTO debug_log (procedure_name, variable_name, variable_value)
    VALUES ('InsertIfNotExists', 'closing procedure', 'closing all');
    COMMIT;
END;
/
create or replace PROCEDURE GetTableData (
    p_tableName    IN VARCHAR2,
    p_orderBy      IN VARCHAR2,
    p_resultSet    OUT SYS_REFCURSOR
)
IS
    v_tableName VARCHAR2(255) := p_tableName;
    v_orderBy   VARCHAR2(255) := p_orderBy;
    sql_stmt    VARCHAR2(1000);

BEGIN
    -- Log
    INSERT INTO debug_log (procedure_name, variable_name, variable_value)
    VALUES ('GetTableData', 'Entering Procedure', 'beginning getting table ' || v_tableName);
    COMMIT;
    
    IF v_tablename = 'SPECIALISTE' THEN
    
        sql_stmt := 'SELECT specialiste.id_specialiste, specialiste.nom_specialiste, specialiste.prenom_specialiste, 
                            specialiste.date_nais_specialiste, specialiste.tel_specialiste, specialiste.email_specialiste, 
                            posseder.id_competence, specialiste.isdeleted_specialiste
                    FROM '  || v_tableName || 
                    ' inner join posseder ON specialiste.id_specialiste = posseder.id_specialiste 
                    ORDER BY ' || v_orderBy;

    ELSIF v_tablename = 'ACTE_MED' THEN

        sql_stmt := 'Select acte_med.ID_ACTE_MED, acte_med.REF_ACTE_MED, acte_med.ID_CLIENT, client.prenom_client, client.nom_client, 
                    acte_med.id_specialiste, specialiste.prenom_specialiste, specialiste.nom_specialiste, acte_med.id_lieu, 
                    lieu.nom_lieu, acte_med.date_debut, acte_med.date_fin, necessiter.id_competence, competence.nom_competence,
                    acte_med.isdeleted_acte_med
                    from acte_med
                    inner join client ON acte_med.id_client = client.id_client
                    inner join specialiste ON acte_med.id_specialiste = specialiste.id_specialiste
                    inner join lieu ON acte_med.id_lieu = lieu.id_lieu
                    inner join necessiter ON acte_med.id_acte_med = necessiter.id_acte_med
                    inner join competence ON necessiter.id_competence = competence.id_competence';

    ELSE
        sql_stmt := 'SELECT * FROM ' || v_tableName || ' ORDER BY ' || v_orderBy;
    END IF;

    OPEN p_resultSet FOR sql_stmt;

    -- Log
    INSERT INTO debug_log (procedure_name, variable_name, variable_value)
    VALUES ('GetTableData', 'Closing Procedure', 'got table ' || v_tableName || ' data' );
    COMMIT;

END;
/
create or replace PROCEDURE SetToIsDeleted (
    p_tableName IN VARCHAR2,
    p_column    IN VARCHAR2,
    p_value     IN VARCHAR2
)
IS
    v_tableName VARCHAR2(255) := p_tableName;
    v_column    VARCHAR2(255) := p_column;
    v_value     VARCHAR2(255) := p_value;
    sql_stmt    VARCHAR2(1000);

BEGIN
    -- Log
    INSERT INTO debug_log (procedure_name, variable_name, variable_value)
    VALUES ('SetToIsDeleted', 'Entering Procedure on ' || v_tableName, 'updating ' || v_value || ' in column : ' || v_column);
    COMMIT;

    sql_stmt := 'UPDATE ' || v_tableName || ' SET ISDELETED_' || v_tableName || ' = 1 WHERE ' || v_column || ' = :1';

    -- Execute the dynamic SQL statement
    EXECUTE IMMEDIATE sql_stmt USING v_value;

END;
/

create or replace PROCEDURE updateData (
    p_tableName     IN VARCHAR2,
    p_column        IN VARCHAR2,
    p_value         IN VARCHAR2,
    p_checkColumn   IN VARCHAR2,
    p_checkValue    IN VARCHAR2
)
IS
    v_tableName     VARCHAR2(255) := p_tableName;
    v_column        VARCHAR2(255) := p_column;
    v_value         VARCHAR2(255) := p_value;
    v_checkColumn   VARCHAR2(255) := p_checkColumn;
    v_checkValue    VARCHAR2(255) := p_checkValue;
    d_value1        DATE;
    sql_stmt        VARCHAR2(1000);

BEGIN
    -- Log
    INSERT INTO debug_log (procedure_name, variable_name, variable_value)
    VALUES ('updateData', 'Entering Procedure on ' || v_tableName,  'updating ' || v_value || ' in column : ' || v_column || 
                                                                        ' of row where column : ' || v_checkColumn || 
                                                                        ' with value : ' || v_checkValue);
    COMMIT;
    
    
    sql_stmt := 'UPDATE ' || v_tableName || ' SET ' || v_column || ' = :1 WHERE ' || v_checkColumn || ' = :2';
    
    CASE
        WHEN v_column = 'DATE_NAIS_CLIENT' OR v_column = 'DATE_NAIS_SPECIALISTE' THEN
            d_value1 := TO_DATE(v_value, 'YYYY-MM-DD');
            EXECUTE IMMEDIATE sql_stmt USING d_value1, v_checkValue;
            
        ELSE
            EXECUTE IMMEDIATE sql_stmt USING v_value, v_checkValue;
    END CASE;
    
END;
/
create or replace PROCEDURE GetIntData (
    p_columnValue  IN VARCHAR2,
    p_tableName    IN VARCHAR2,
    p_checkColumn  IN VARCHAR2,
    p_checkValue   IN VARCHAR2,
    p_resultValue  OUT NUMBER
)
IS
    v_columnValue   VARCHAR2(255)   := p_columnValue;
    v_tableName     VARCHAR2(255)   := p_tableName;
    v_checkColumn   VARCHAR2(255)   := p_checkColumn;
    v_checkValue    VARCHAR2(255)   := p_checkValue;
    sql_stmt        VARCHAR2(1000)  ;

BEGIN
    -- Log
    INSERT INTO debug_log (procedure_name, variable_name, variable_value)
    VALUES ('GetIntData', 'Entering Procedure', 'beginning getting table ' || v_columnValue);
    INSERT INTO debug_log (procedure_name, variable_name, variable_value)
    VALUES ('GetIntData', 'v_columnValue', v_columnValue);
    INSERT INTO debug_log (procedure_name, variable_name, variable_value)
    VALUES ('GetIntData', 'v_tableName', v_tableName);
    INSERT INTO debug_log (procedure_name, variable_name, variable_value)
    VALUES ('GetIntData', 'v_checkColumn', v_checkColumn);
    INSERT INTO debug_log (procedure_name, variable_name, variable_value)
    VALUES ('GetIntData', 'v_checkValue', v_checkValue);
    COMMIT;

    sql_stmt := 'SELECT ' || v_columnValue || ' FROM ' || v_tableName || ' WHERE ' || v_checkColumn || ' = ''' || v_checkValue || '''';

    EXECUTE IMMEDIATE sql_stmt INTO p_resultValue;
    
        INSERT INTO debug_log (procedure_name, variable_name, variable_value)
        VALUES ('GetIntData', 'p_resultValue', p_resultValue);
        COMMIT;

END;
/
create or replace TYPE NumberList IS TABLE OF NUMBER;
/
create or replace PROCEDURE GetSpecialisteForCompetence (
    p_checkValue   IN INT,
    p_resultValues OUT NumberList
)
IS
    v_checkValue INT := p_checkValue;
    sql_stmt VARCHAR2(1000);

BEGIN
    -- Log
    INSERT INTO debug_log (procedure_name, variable_name, variable_value)
    VALUES ('GetSpecialisteForCompetence', 'v_checkValue', v_checkValue);
    COMMIT;

    sql_stmt := 'SELECT DISTINCT ID_Specialiste FROM Posseder WHERE ID_Competence = :checkValue';

    EXECUTE IMMEDIATE sql_stmt BULK COLLECT INTO p_resultValues USING v_checkValue;

    -- Log
    FOR i IN 1..p_resultValues.COUNT LOOP
        INSERT INTO debug_log (procedure_name, variable_name, variable_value)
        VALUES ('GetSpecialisteForCompetence', 'p_resultValues(' || i || ')', p_resultValues(i));
    END LOOP;

    COMMIT;
END;
/
create or replace PROCEDURE GetStringData (
    p_columnValue  IN VARCHAR2,
    p_tableName    IN VARCHAR2,
    p_checkColumn  IN VARCHAR2,
    p_checkValue   IN VARCHAR2,
    p_resultValue  OUT NUMBER
)
IS
    v_columnValue   VARCHAR2(255)   := p_columnValue;
    v_tableName     VARCHAR2(255)   := p_tableName;
    v_checkColumn   VARCHAR2(255)   := p_checkColumn;
    v_checkValue    VARCHAR2(255)   := p_checkValue;
    sql_stmt        VARCHAR2(1000)  ;

BEGIN
    -- Log
    INSERT INTO debug_log (procedure_name, variable_name, variable_value)
    VALUES ('GetIntData', 'Entering Procedure', 'beginning getting table ' || v_columnValue);
    INSERT INTO debug_log (procedure_name, variable_name, variable_value)
    VALUES ('GetIntData', 'v_columnValue', v_columnValue);
    INSERT INTO debug_log (procedure_name, variable_name, variable_value)
    VALUES ('GetIntData', 'v_tableName', v_tableName);
    INSERT INTO debug_log (procedure_name, variable_name, variable_value)
    VALUES ('GetIntData', 'v_checkColumn', v_checkColumn);
    INSERT INTO debug_log (procedure_name, variable_name, variable_value)
    VALUES ('GetIntData', 'v_checkValue', v_checkValue);
    COMMIT;

    sql_stmt := 'SELECT ' || v_columnValue || ' FROM ' || v_tableName || ' WHERE ' || v_checkColumn || ' = ''' || v_checkValue || '''';

    EXECUTE IMMEDIATE sql_stmt INTO p_resultValue;
    
    INSERT INTO debug_log (procedure_name, variable_name, variable_value)
    VALUES ('GetIntData', 'p_resultValue', p_resultValue);
    COMMIT;

END;
/
create or replace PROCEDURE test (
    p_resultSet    OUT SYS_REFCURSOR
)
IS
    sql_stmt    VARCHAR2(1000);

BEGIN

    sql_stmt := 'Select * from necessiter';

    OPEN p_resultSet FOR sql_stmt;

END;
/
create or replace PROCEDURE InsertNecessiter (
    p_tableName IN VARCHAR2,
    p_columns   IN VARCHAR2,
    p_value     IN INT
)
IS
    v_tableName VARCHAR2(255)   := p_tableName;
    v_columns   VARCHAR2(255)   := p_columns;
    v_value     INT             := p_value;
    sql_stmt VARCHAR2(1000);
BEGIN
    -- Logging
    INSERT INTO debug_log (procedure_name, variable_name, variable_value)
    VALUES ('InsertRecord', 'InsertNecessiter Procedure', 'insert started');
    COMMIT;

    sql_stmt := 'INSERT INTO ' || v_tableName || ' (' || v_columns || ') ' ||
                'VALUES (:1)';

    EXECUTE IMMEDIATE sql_stmt USING v_value;

    -- Logging
    INSERT INTO debug_log (procedure_name, variable_name, variable_value)
    VALUES ('InsertRecord', 'InsertNecessiter Procedure', 'insert finished');
    COMMIT;
END;
/
create or replace PROCEDURE lastIdAm (
    p_columnValue  IN VARCHAR2,
    p_tableName    IN VARCHAR2,
    p_resultValue OUT INT
)
IS
    v_columnValue   VARCHAR2(255)   := p_columnValue;
    v_tableName     VARCHAR2(255)   := p_tableName;
    sql_stmt        VARCHAR2(1000)  ;

BEGIN
    -- Log
    INSERT INTO debug_log (procedure_name, variable_name, variable_value)
    VALUES ('GetIntData', 'Entering Procedure', 'beginning getting table ' || v_columnValue);
    INSERT INTO debug_log (procedure_name, variable_name, variable_value)
    VALUES ('GetIntData', 'v_columnValue', v_columnValue);
    INSERT INTO debug_log (procedure_name, variable_name, variable_value)
    VALUES ('GetIntData', 'v_tableName', v_tableName);
    COMMIT;

    sql_stmt := 'SELECT MAX(' || v_columnValue || ') FROM ' || v_tableName;

    EXECUTE IMMEDIATE sql_stmt INTO p_resultValue;

    INSERT INTO debug_log (procedure_name, variable_name, variable_value)
    VALUES ('GetIntData', 'p_resultValue', p_resultValue);
    COMMIT;

END;
/