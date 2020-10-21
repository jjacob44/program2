CREATE OR REPLACE TRIGGER classsize_after_insert
AFTER INSERT ON enrollments 
FOR EACH ROW
DECLARE 
	cid varchar2(10);
BEGIN 
	cid := :new.classid;
	/*SELECT classid INTO cid from enrollments;
	SELECT * from students;
	cid := classid;*/
	UPDATE classes
	SET class_size = class_size + 1
	WHERE classid = cid;
END;
/


