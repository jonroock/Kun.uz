insert into profile(id,name,surname,username,password,status,visible, created_date)
values (100,'Admin','Adminjon','admin@gmail.com','$2a$10$rylKPXFYhpNGM8UEkYfRVO7Ke0D8gLLYu42ncZCL4R0DzLoEd7JAS','ACTIVE',true, now());

SELECT setval('profile_id_seq', (SELECT MAX(id) FROM profile));


insert into profile_role(id,profile_id,roles) values (55,100,'ROLE_ADMIN');
insert into profile_role(id,profile_id,roles) values (56,100,'ROLE_MODERATOR');

SELECT setval('profile_role_id_seq', (SELECT MAX(id) FROM profile_role));




