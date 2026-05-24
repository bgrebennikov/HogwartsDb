-- Student table

alter table student alter column name set not null;

alter table student add constraint uq_student_name unique (name);

alter table  student add constraint chK_student_age check ( age >= 16 );

alter table student alter column age set default 20;

-- Faculty table

alter table faculty add constraint uq_faculty_name_color unique (name, color);