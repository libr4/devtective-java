CREATE EXTENSION IF NOT EXISTS pgcrypto; -- provides gen_random_uuid()

ALTER TABLE public.app_user
  ADD COLUMN public_id UUID NOT NULL DEFAULT gen_random_uuid();
CREATE UNIQUE INDEX ux_app_user_public_id ON public.app_user(public_id);

ALTER TABLE public.workspace
  ADD COLUMN public_id UUID NOT NULL DEFAULT gen_random_uuid();
CREATE UNIQUE INDEX ux_workspace_public_id ON public.workspace(public_id);

ALTER TABLE public.project
  ADD COLUMN public_id UUID NOT NULL DEFAULT gen_random_uuid();
CREATE UNIQUE INDEX ux_project_public_id ON public.project(public_id);

ALTER TABLE public.task
  ADD COLUMN public_id UUID NOT NULL DEFAULT gen_random_uuid();
CREATE UNIQUE INDEX ux_task_public_id ON public.task(public_id);

ALTER TABLE public.workspace_member
  ADD COLUMN public_id UUID NOT NULL DEFAULT gen_random_uuid();
CREATE UNIQUE INDEX ux_workspace_member_public_id ON public.workspace_member(public_id);