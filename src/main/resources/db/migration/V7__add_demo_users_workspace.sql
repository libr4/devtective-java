INSERT INTO workspace (name)
VALUES ('PUBLIC_DEMO')
ON CONFLICT DO NOTHING;

INSERT INTO public.workspace_member (workspace_id, worker_id)
SELECT ws.workspace_id, w.worker_id
FROM workspace ws
JOIN app_user u ON TRUE
JOIN worker w ON w.user_id = u.user_id
WHERE ws.name = 'PUBLIC_DEMO'
  AND u.username ILIKE 'demo.%'
ON CONFLICT DO NOTHING;