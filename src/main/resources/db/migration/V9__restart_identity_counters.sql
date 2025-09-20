SELECT setval(
  pg_get_serial_sequence('public.project_member','member_id'),
  COALESCE((SELECT MAX(member_id) FROM public.project_member), 0) + 1,
  false
);

SELECT setval(
  pg_get_serial_sequence('public.project_leader','leader_id'),
  COALESCE((SELECT MAX(leader_id) FROM public.project_leader), 0) + 1,
  false
);
