INSERT INTO public.user_discoverability (id, code, name)
VALUES (2, 'WORKSPACE')
ON CONFLICT (id) DO NOTHING;

UPDATE public.app_user
SET discoverability_id = 2
WHERE discoverability_id IS NULL OR discoverability_id = 1;

ALTER TABLE public.app_user
  ALTER COLUMN discoverability_id SET DEFAULT 2;

-- seed_demo_users.sql
-- Code below creates four demo users and their worker profiles
CREATE EXTENSION IF NOT EXISTS pgcrypto;

INSERT INTO public.app_user (username, email, password_hash, role_id, discoverability_id, created_at)
VALUES
  ('demo.alice', 'alice@demo.local', crypt('demo', gen_salt('bf')), 3, 3, now()),
  ('demo.bob',   'bob@demo.local',   crypt('demo', gen_salt('bf')), 3, 3, now()),
  ('demo.carol', 'carol@demo.local', crypt('demo', gen_salt('bf')), 3, 3, now()),
  ('demo.dave',  'dave@demo.local',  crypt('demo', gen_salt('bf')), 3, 3, now())
ON CONFLICT (username) DO NOTHING;

INSERT INTO public.worker (user_id, first_name, last_name)
SELECT u.user_id, 'Alice', 'Almeida'
FROM public.app_user u
WHERE u.username = 'demo.alice'
  AND NOT EXISTS (SELECT 1 FROM public.worker w WHERE w.user_id = u.user_id);

INSERT INTO public.worker (user_id, first_name, last_name)
SELECT u.user_id, 'Bob', 'Barbosa'
FROM public.app_user u
WHERE u.username = 'demo.bob'
  AND NOT EXISTS (SELECT 1 FROM public.worker w WHERE w.user_id = u.user_id);

INSERT INTO public.worker (user_id, first_name, last_name)
SELECT u.user_id, 'Carol', 'Costa'
FROM public.app_user u
WHERE u.username = 'demo.carol'
  AND NOT EXISTS (SELECT 1 FROM public.worker w WHERE w.user_id = u.user_id);

INSERT INTO public.worker (user_id, first_name, last_name)
SELECT u.user_id, 'Dave', 'Dias'
FROM public.app_user u
WHERE u.username = 'demo.dave'
  AND NOT EXISTS (SELECT 1 FROM public.worker w WHERE w.user_id = u.user_id);
