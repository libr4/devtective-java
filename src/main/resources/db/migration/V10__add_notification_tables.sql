--
-- Create notification media lookup table
--
CREATE TABLE public.notification_media (
    id SERIAL PRIMARY KEY,
    name character varying(255) NOT NULL
);

--
-- Create notification status lookup table
--
CREATE TABLE public.notification_status (
    id SERIAL PRIMARY KEY,
    name character varying(255) NOT NULL
);

--
-- Create notification type lookup table
--
CREATE TABLE public.notification_type (
    id SERIAL PRIMARY KEY,
    name character varying(255) NOT NULL
);

--
-- Create notification table
--
CREATE TABLE public.notification (
    id BIGSERIAL PRIMARY KEY,
    recipient_id bigint NOT NULL,
    messagem character varying(255) NOT NULL,
    media_id integer NOT NULL REFERENCES public.notification_media(id),
    status_id integer NOT NULL REFERENCES public.notification_status(id),
    type_id integer NOT NULL REFERENCES public.notification_type(id),
    CONSTRAINT fk_notification_recipient_id FOREIGN KEY (recipient_id) REFERENCES public.app_user(user_id)
);

--
-- Populate notification_media with enum values
--
INSERT INTO public.notification_media (name) VALUES ('EMAIL');
INSERT INTO public.notification_media (name) VALUES ('SMS');
INSERT INTO public.notification_media (name) VALUES ('PUSH');
INSERT INTO public.notification_media (name) VALUES ('IN_APP');

--
-- Populate notification_status with enum values
--
INSERT INTO public.notification_status (name) VALUES ('PENDING');
INSERT INTO public.notification_status (name) VALUES ('SENT');
INSERT INTO public.notification_status (name) VALUES ('FAILED');
INSERT INTO public.notification_status (name) VALUES ('READ');
INSERT INTO public.notification_status (name) VALUES ('DISMISSED');

--
-- Populate notification_type with enum values
--
INSERT INTO public.notification_type (name) VALUES ('PROJECT_INVITE');
INSERT INTO public.notification_type (name) VALUES ('REMINDER');
INSERT INTO public.notification_type (name) VALUES ('ALERT');
INSERT INTO public.notification_type (name) VALUES ('MESSAGE');
INSERT INTO public.notification_type (name) VALUES ('TASK_UPDATE');
