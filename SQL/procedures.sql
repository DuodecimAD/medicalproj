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
    VALUES ('CheckIfExists', 'closing procedure', v_value || ' dont exist : closing');
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
    
    sql_stmt := 'INSERT INTO ' || p_tableName || ' (' || v_columns || ') ' ||
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
    p_columnsLength IN INT,
    p_values        IN VARCHAR2
)
IS 
    recordExists    INT;
    tableName       VARCHAR2(255)   := p_tableName;
    inputColumns    VARCHAR2(255)   := p_columns;
    columnsLength   INT             := p_columnsLength;
    inputValues     VARCHAR2(255)   := p_values;
    separator       VARCHAR2(1)     := ',';
    startPos        INT             := 1;
    endPos          INT;
    countPos        INT;
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
BEGIN

    -- Log
    INSERT INTO debug_log (procedure_name, variable_name, variable_value)
    VALUES ('InsertIfNotExists', 'Entering Procedure', 'start ');
    INSERT INTO debug_log (procedure_name, variable_name, variable_value)
    VALUES ('InsertIfNotExists', 'tablename', tablename);
    INSERT INTO debug_log (procedure_name, variable_name, variable_value)
    VALUES ('InsertIfNotExists', 'inputColumns', inputColumns);
    INSERT INTO debug_log (procedure_name, variable_name, variable_value)
    VALUES ('InsertIfNotExists', 'columnsLength', columnsLength);
    INSERT INTO debug_log (procedure_name, variable_name, variable_value)
    VALUES ('InsertIfNotExists', 'inputValues', inputValues);
    COMMIT; 


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
        VALUES ('InsertIfNotExists', 'c_value1', c_value1);
        INSERT INTO debug_log (procedure_name, variable_name, variable_value)
        VALUES ('InsertIfNotExists', 'c_value2', c_value2);
        INSERT INTO debug_log (procedure_name, variable_name, variable_value)
        VALUES ('InsertIfNotExists', 'c_value3', c_value3);
        INSERT INTO debug_log (procedure_name, variable_name, variable_value)
        VALUES ('InsertIfNotExists', 'c_value4', c_value4);
        INSERT INTO debug_log (procedure_name, variable_name, variable_value)
        VALUES ('InsertIfNotExists', 'c_value5', c_value5);
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


        -- Check if the record exists
        CheckIfExists(tableName, c_value4, s_value4);
        -- Check if the record exists
        CheckIfExists(tableName, c_value5, s_value5);

        -- Convert the string to a DATE
        d_value1 := TO_DATE(s_value3, 'YYYY-MM-DD'); 
        
        -- Log
        INSERT INTO debug_log (procedure_name, variable_name, variable_value)
        VALUES ('InsertIfNotExists', 'd_value1', d_value1);
        COMMIT;

        -- Record does not exist, proceed with the insert
        InsertRecord(tableName, inputColumns, s_value1, s_value2, d_value1, s_value4, s_value5);

    --END IF;
    
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

     sql_stmt := 'SELECT * FROM ' || v_tableName || ' ORDER BY ' || v_orderBy;
     
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

    INSERT INTO debug_log (procedure_name, variable_name, variable_value)
    VALUES ('GetIntData', 'p_resultValue', p_resultValue);
    COMMIT;

    EXECUTE IMMEDIATE sql_stmt INTO p_resultValue;

END;
/