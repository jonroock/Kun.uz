CREATE OR REPLACE FUNCTION article_like_function()
RETURNS TRIGGER 
LANGUAGE PLPGSQL 
AS $$
BEGIN
    IF TG_OP = 'INSERT' then
		IF NEW.emotion = 'LIKE' then
update article set like_count = COALESCE(like_count,0) + 1 where id = NEW.article_id;
elseif NEW.emotion = 'DISLIKE' then
update article set dislike_count = COALESCE(dislike_count,0) + 1 where id = NEW.article_id;
end if;
	ELSEIF TG_OP = 'UPDATE'then
		 IF NEW.emotion = 'LIKE' and OLD.emotion = 'DISLIKE' then
update article set like_count = like_count + 1, dislike_count = COALESCE(dislike_count,0) - 1  where id = NEW.article_id;
elseif NEW.emotion = 'DISLIKE' and OLD.emotion = 'LIKE' then
update article set like_count = like_count - 1, dislike_count = COALESCE(dislike_count,0) + 1  where id = NEW.article_id;
end if;
	ELSEIF TG_OP = 'DELETE' then
		 IF OLD.emotion = 'LIKE' then
update article set like_count = COALESCE(like_count,0) - 1 where id = OLD.article_id;
elseif OLD.emotion = 'DISLIKE' then
update article set dislike_count = COALESCE(dislike_count,0) - 1  where id = OLD.article_id;
end if;
return OLD;
end if;
return NEW;
END; $$