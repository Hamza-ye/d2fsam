CREATE
OR REPLACE FUNCTION JSONB_HAS_TEAMS_IDS(JSONB, text)
RETURNS BOOL AS $$
SELECT $1 - > 'teams' ?| $2::text[];
$$
LANGUAGE SQL IMMUTABLE PARALLEL SAFE;

CREATE
OR REPLACE FUNCTION JSONB_CHECK_TEAMS_ACCESS(JSONB, text, text)
RETURNS BOOL AS $$
SELECT exists(
               SELECT 1
               FROM jsonb_each($1 - > 'teams') je
               WHERE je.key = ANY ($3::text[])
                 AND je.value ->>'access' LIKE $2
     );
$$
LANGUAGE SQL IMMUTABLE PARALLEL SAFE;
