select
    s.name as student_name,
    s.age as student_age,
    f.name as faculty_name
from student s
left join faculty f on s.faculty_id = f.id;

select
    s.name as student_name,
    s.age as student_age,
    s.id as student_id
from student s 
left join avatar a on a.student_id = s.id;