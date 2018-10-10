CREATE VIEW vw_accepted_participants AS
SELECT users.user_id, lastname, firstname, date_id
FROM users
INNER JOIN participant_date 
ON users.user_id = participant_date.user_id
AND users.deleted = 0
AND participant_date.deleted = 0
AND participant_date.participant_state_id IN (1,2)
ORDER BY lastname, firstname;

CREATE VIEW vw_networks_and_their_participants AS
SELECT networks.name AS network, dates.year_code AS year, users.lastname AS lastname, users.firstname AS firstname, users.email AS email 
FROM networks 
INNER JOIN dates ON networks.date_id = dates.date_id  
INNER JOIN session_in_network ON networks.network_id = session_in_network.network_id  
INNER JOIN sessions ON session_in_network.session_id = sessions.session_id  
INNER JOIN session_participant ON sessions.session_id = session_participant.session_id  
INNER JOIN participant_date ON session_participant.user_id = participant_date.user_id  
INNER JOIN users ON participant_date.user_id = users.user_id  
WHERE networks.deleted = 0 
AND networks.show_online = 1
AND sessions.session_state_id = 2
AND sessions.deleted = 0
AND session_participant.date_id = networks.date_id
AND participant_date.deleted = 0
AND participant_date.date_id = networks.date_id
AND participant_date.participant_state_id IN (0,1,2,999)
AND users.deleted = 0
AND users.email_discontinued = 0
GROUP BY networks.name, dates.year_code, users.lastname, users.firstname, users.email 
ORDER BY networks.name, dates.year_code, users.lastname, users.firstname, users.email;

CREATE VIEW vw_session_participant_papers AS
SELECT session_participant_id, paper_id
FROM session_participant
INNER JOIN papers
ON session_participant.user_id = papers.user_id
AND session_participant.date_id = papers.date_id
AND session_participant.session_id = papers.session_id
AND papers.deleted = 0;

CREATE VIEW vw_combined_session_participants AS
SELECT CAST(CONCAT(date_id, user_id, session_id, participant_type_id) AS UNSIGNED) AS id, date_id, user_id, session_id, participant_type_id, session_participant_id, added_by
FROM session_participant
UNION ALL
SELECT CAST(CONCAT(paper_coauthors.date_id, paper_coauthors.user_id, session_id, 9) AS UNSIGNED) AS id, paper_coauthors.date_id, paper_coauthors.user_id, session_id, 9, NULL, paper_coauthors.added_by
FROM paper_coauthors
INNER JOIN papers
ON paper_coauthors.paper_id = papers.paper_id;
