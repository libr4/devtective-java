BEGIN;

DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint
    WHERE conname = 'uq_project_ws'
      AND conrelid = 'public.project'::regclass
  ) THEN
    ALTER TABLE public.project
      ADD CONSTRAINT uq_project_ws UNIQUE (workspace_id, project_id);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint
    WHERE conname = 'uq_workspace_member_ws_worker'
      AND conrelid = 'public.workspace_member'::regclass
  ) THEN
    ALTER TABLE public.workspace_member
      ADD CONSTRAINT uq_workspace_member_ws_worker UNIQUE (workspace_id, worker_id);
  END IF;
END $$;

INSERT INTO public.workspace_member (workspace_id, worker_id)
SELECT DISTINCT x.workspace_id, x.worker_id
FROM (
  SELECT workspace_id, worker_id FROM public.project_member
  UNION
  SELECT workspace_id, worker_id FROM public.project_leader
) AS x
WHERE NOT EXISTS (
  SELECT 1 FROM public.workspace_member wm
  WHERE wm.workspace_id = x.workspace_id AND wm.worker_id = x.worker_id
);

DO $$
DECLARE
  rec RECORD;
BEGIN
  FOR rec IN
    SELECT n.nspname, t.relname, c.conname
    FROM pg_constraint c
    JOIN pg_class t ON t.oid = c.conrelid
    JOIN pg_namespace n ON n.oid = t.relnamespace
    WHERE n.nspname = 'public'
      AND t.relname IN ('project_member', 'project_leader')
      AND EXISTS (
        SELECT 1
        FROM pg_attribute a
        WHERE a.attrelid = c.conrelid
          AND a.attname = 'workspace_member_id'
          AND a.attnum = ANY (c.conkey)
      )
  LOOP
    EXECUTE format('ALTER TABLE %I.%I DROP CONSTRAINT %I', rec.nspname, rec.relname, rec.conname);
  END LOOP;
END $$;

ALTER TABLE public.project_member DROP COLUMN IF EXISTS workspace_member_id;
ALTER TABLE public.project_leader DROP COLUMN IF EXISTS workspace_member_id;

ALTER TABLE public.project_member
  ALTER COLUMN workspace_id SET NOT NULL,
  ALTER COLUMN project_id   SET NOT NULL,
  ALTER COLUMN worker_id    SET NOT NULL;

ALTER TABLE public.project_leader
  ALTER COLUMN workspace_id SET NOT NULL,
  ALTER COLUMN project_id   SET NOT NULL,
  ALTER COLUMN worker_id    SET NOT NULL;

DO $$
BEGIN
  IF EXISTS (
    SELECT 1 FROM public.project_member
    GROUP BY workspace_id, project_id, worker_id
    HAVING COUNT(*) > 1
  ) THEN
    RAISE EXCEPTION 'Duplicate (workspace_id, project_id, worker_id) rows in project_member';
  END IF;

  IF EXISTS (
    SELECT 1 FROM public.project_leader
    GROUP BY workspace_id, project_id, worker_id
    HAVING COUNT(*) > 1
  ) THEN
    RAISE EXCEPTION 'Duplicate (workspace_id, project_id, worker_id) rows in project_leader';
  END IF;
END $$;

ALTER TABLE public.project_member
  ADD CONSTRAINT fk_pm_proj_ws
    FOREIGN KEY (workspace_id, project_id)
    REFERENCES public.project (workspace_id, project_id)
    ON DELETE CASCADE,
  ADD CONSTRAINT fk_pm_ws_worker
    FOREIGN KEY (workspace_id, worker_id)
    REFERENCES public.workspace_member (workspace_id, worker_id)
    ON DELETE CASCADE,
  ADD CONSTRAINT uq_pm UNIQUE (workspace_id, project_id, worker_id);

ALTER TABLE public.project_leader
  ADD CONSTRAINT fk_pl_proj_ws
    FOREIGN KEY (workspace_id, project_id)
    REFERENCES public.project (workspace_id, project_id)
    ON DELETE CASCADE,
  ADD CONSTRAINT fk_pl_ws_worker
    FOREIGN KEY (workspace_id, worker_id)
    REFERENCES public.workspace_member (workspace_id, worker_id)
    ON DELETE CASCADE,
  ADD CONSTRAINT uq_pl UNIQUE (workspace_id, project_id, worker_id);

COMMIT;
