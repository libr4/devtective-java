DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'task_status_name_key') THEN
    ALTER TABLE public.task_status ADD CONSTRAINT task_status_name_key UNIQUE (name);
  END IF;

  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'task_priority_name_key') THEN
    ALTER TABLE public.task_priority ADD CONSTRAINT task_priority_name_key UNIQUE (name);
  END IF;

  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'task_type_name_key') THEN
    ALTER TABLE public.task_type ADD CONSTRAINT task_type_name_key UNIQUE (name);
  END IF;
END$$;

-- roles (explicit IDs, keep your numbers including 0)
INSERT INTO public.role (role_id, role_name) VALUES
  (0, 'SUPER_ADMIN'),
  (1, 'ADMIN'),
  (2, 'MANAGER'),
  (3, 'USER'),
  (4, 'READER')
ON CONFLICT (role_id) DO UPDATE SET role_name = EXCLUDED.role_name;
-- ON CONFLICT (role_name) DO NOTHING;

-- positions
INSERT INTO public.position (position_id, position_name) VALUES
  (1, 'Developer'),
  (2, 'Tester'),
  (3, 'Project Manager'),
  (4, 'Requirements Analyst')
ON CONFLICT (position_id) DO UPDATE SET position_name = EXCLUDED.position_name;
-- ON CONFLICT (position_name) DO NOTHING;

-- task types
INSERT INTO public.task_type (task_type_id, name) VALUES
  (1, 'Task'),
  (2, 'Bug'),
  (3, 'Functionality'),
  (4, 'Error')
ON CONFLICT (task_type_id) DO UPDATE SET name = EXCLUDED.name;
-- ON CONFLICT (name) DO NOTHING;

-- task priorities
INSERT INTO public.task_priority (task_priority_id, name) VALUES
  (1, 'Very Low'),
  (2, 'Low'),
  (3, 'Medium'),
  (4, 'High'),
  (5, 'Very High'),
  (6, 'Critical')
ON CONFLICT (task_priority_id) DO UPDATE SET name = EXCLUDED.name;
-- ON CONFLICT (name) DO NOTHING;

-- task status
INSERT INTO public.task_status (task_status_id, name) VALUES
  (1, 'Open'),
  (2, 'In Progress'),
  (3, 'Tests'),
  (4, 'Blocked'),
  (5, 'Completed')
ON CONFLICT (task_status_id) DO UPDATE SET name = EXCLUDED.name;
-- ON CONFLICT (name) DO NOTHING;

-- advance sequences to MAX(id) so future inserts without explicit IDs won’t collide
-- Only if these sequences exist in your schema names
SELECT setval('role_role_id_seq',        (SELECT MAX(role_id) FROM public.role));
SELECT setval('position_position_id_seq',(SELECT MAX(position_id) FROM public.position));
SELECT setval('task_type_task_type_id_seq',(SELECT MAX(task_type_id) FROM public.task_type));
SELECT setval('task_priority_task_priority_id_seq',(SELECT MAX(task_priority_id) FROM public.task_priority));
SELECT setval('task_status_task_status_id_seq',(SELECT MAX(task_status_id) FROM public.task_status));

-- SELECT setval('public.role_role_id_seq',        (SELECT GREATEST(COALESCE(MAX(role_id), 0), 1)        FROM public.role),        true);
-- SELECT setval('public.position_position_id_seq',(SELECT GREATEST(COALESCE(MAX(position_id), 0), 1)     FROM public."position"),  true);
-- SELECT setval('public.task_type_task_type_id_seq',(SELECT GREATEST(COALESCE(MAX(task_type_id), 0), 1)  FROM public.task_type),   true);
-- SELECT setval('public.task_priority_task_priority_id_seq',(SELECT GREATEST(COALESCE(MAX(task_priority_id), 0), 1) FROM public.task_priority), true);
-- SELECT setval('public.task_status_task_status_id_seq',(SELECT GREATEST(COALESCE(MAX(task_status_id), 0), 1) FROM public.task_status), true);
