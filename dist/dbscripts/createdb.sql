-- Create tables
---------------------------

CREATE TABLE tasks (
  id uuid NOT NULL,
  task_status integer NOT NULL,
  task_type integer NOT NULL,
  backup_name character varying(255),
  create_time timestamp with time zone,
  last_update timestamp with time zone
);

CREATE TABLE vm_policies (
  id uuid NOT NULL,
  enabled boolean DEFAULT false NOT NULL,
  backup_method integer NOT NULL,
  time_of_day character varying(8) NOT NULL,
  week_days character varying(8) NOT NULL,
  auto_delete_reserve_policy integer NOT NULL,
  auto_delete_reserve_amount integer NOT NULL
);

ALTER TABLE ONLY tasks
    ADD CONSTRAINT tasks_pk PRIMARY KEY (id);

ALTER TABLE ONLY vm_policies
    ADD CONSTRAINT vm_policies_pk PRIMARY KEY (id, backup_method);


CREATE OR REPLACE FUNCTION public.create_plpgsql_language ()
    RETURNS TEXT
    AS $$
        CREATE LANGUAGE plpgsql;
        SELECT 'language plpgsql created'::TEXT;
    $$
LANGUAGE 'sql';

SELECT CASE WHEN
      (SELECT true::BOOLEAN
        FROM pg_language
        WHERE lanname='plpgsql')
    THEN
      (SELECT 'language already installed'::TEXT)
    ELSE
      (SELECT public.create_plpgsql_language())
    END;

DROP FUNCTION public.create_plpgsql_language ();

-- Create Task functions
---------------------------

CREATE OR REPLACE FUNCTION saveTask(v_id UUID,
  v_task_status INTEGER,
  v_task_type INTEGER,
  v_backup_name VARCHAR(255),
  v_create_time timestamp,
  v_last_update timestamp)
RETURNS VOID
  AS $procedure$
BEGIN
  INSERT INTO tasks(id, task_status, task_type, backup_name, create_time, last_update)
  VALUES(v_id, v_task_status, v_task_type, v_backup_name, v_create_time, v_last_update);

END; $procedure$
LANGUAGE plpgsql;


DROP TYPE IF EXISTS task CASCADE;
CREATE TYPE task AS (
  id uuid,
  task_status integer,
  task_type integer,
  backup_name character varying(255),
  create_time timestamp with time zone,
  last_update timestamp with time zone
);

CREATE OR REPLACE FUNCTION getTaskById(v_id UUID) RETURNS SETOF task STABLE
  AS $procedure$
BEGIN
RETURN QUERY SELECT *
             FROM tasks task
             WHERE task.id = v_id;
END; $procedure$
LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION getOldestTaskTypeWithStatus(
  v_task_type INTEGER,
  v_task_status INTEGER)
RETURNS SETOF task STABLE
  AS $procedure$
BEGIN
RETURN QUERY SELECT *
             FROM tasks task
             WHERE task.task_type = v_task_type
             AND task.task_status = v_task_status
             ORDER BY task.create_time DESC
             LIMIT 1;
END; $procedure$
LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION updateTask(v_id UUID,
  v_task_status INTEGER,
  v_task_type INTEGER,
  v_backup_name VARCHAR(255),
  v_create_time timestamp,
  v_last_update timestamp)
RETURNS VOID
 AS $procedure$
BEGIN
  UPDATE tasks
  SET task_status = v_task_status,
      task_type = v_task_type,
      backup_name = v_backup_name,
      create_time = v_create_time,
      last_update = v_last_update
  WHERE id = v_id;
END; $procedure$
LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION deleteTask(v_id UUID) RETURNS VOID
  AS $procedure$
BEGIN
  DELETE FROM tasks
  WHERE id = v_id;
END; $procedure$
LANGUAGE plpgsql;


-- Create VmPolicy functions
---------------------------

CREATE OR REPLACE FUNCTION saveVmPolicy(v_id UUID,
  v_enabled BOOLEAN,
  v_backup_method INTEGER,
  v_time_of_day VARCHAR(255),
  v_week_days VARCHAR(8),
  v_auto_delete_reserve_policy INTEGER,
  v_auto_delete_reserve_amount INTEGER)
RETURNS VOID
  AS $procedure$
BEGIN
  INSERT INTO vm_policies(id, enabled, backup_method, time_of_day, week_days, auto_delete_reserve_policy, auto_delete_reserve_amount)
  VALUES(v_id, v_enabled, v_backup_method, v_time_of_day, v_week_days, v_auto_delete_reserve_policy, v_auto_delete_reserve_amount);

END; $procedure$
LANGUAGE plpgsql;


DROP TYPE IF EXISTS vm_policy CASCADE;
CREATE TYPE vm_policy AS (
  id uuid,
  enabled boolean,
  backup_method integer,
  time_of_day character varying(8),
  week_days character varying(8),
  auto_delete_reserve_policy integer,
  auto_delete_reserve_amount integer
);

CREATE OR REPLACE FUNCTION getVmPolicyById(v_id UUID) RETURNS SETOF vm_policy STABLE
  AS $procedure$
BEGIN
RETURN QUERY SELECT *
             FROM vm_policies vm_policy
             WHERE vm_policy.id = v_id;
END; $procedure$
LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION updateVmPolicy(v_id UUID,
  v_enabled BOOLEAN,
  v_backup_method INTEGER,
  v_time_of_day VARCHAR(255),
  v_week_days VARCHAR(8),
  v_auto_delete_reserve_policy INTEGER,
  v_auto_delete_reserve_amount INTEGER)
RETURNS VOID
 AS $procedure$
BEGIN
  UPDATE vm_policies
  SET enabled = v_enabled,
      backup_method = v_backup_method,
      time_of_day = v_time_of_day,
      week_days = v_week_days,
      auto_delete_reserve_policy = v_auto_delete_reserve_policy,
      auto_delete_reserve_amount = v_auto_delete_reserve_amount
  WHERE id = v_id;
END; $procedure$
LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION deleteVmPolicy(v_id UUID) RETURNS VOID
  AS $procedure$
BEGIN
  DELETE FROM vm_policies
  WHERE id = v_id;
END; $procedure$
LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION getScheduleVms() RETURNS SETOF vm_policy STABLE
  AS $procedure$
BEGIN
RETURN QUERY SELECT *
          FROM vm_policies vm_policy
          WHERE vm_policy.enabled = true;
END; $procedure$
LANGUAGE plpgsql;
