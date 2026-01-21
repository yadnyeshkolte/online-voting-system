---
title: "Election Insertion Queries"
permalink: /docs/database/electioninsertion/
excerpt: "Insertion queries for Elections, Candidates, Votes, and Results"
last_modified_at: 2026-01-21
toc: true
---

## Election Data Insertion

```sql




-- 3. INSERT 2 ACTIVE ELECTIONS AND 2 PAST ELECTIONS
INSERT INTO elections (election_name, election_type, start_date, end_date, status, result_published, result_published_at, result_published_by, created_by) VALUES
-- Active Elections
('Maharashtra State Assembly Election 2025', 'STATE', '2025-11-20 08:00:00', '2025-11-25 18:00:00', 'ACTIVE', FALSE, NULL, NULL, 1),
('Mumbai Municipal Corporation Election 2025', 'LOCAL', '2025-11-21 08:00:00', '2025-11-24 18:00:00', 'ACTIVE', FALSE, NULL, NULL, 2),
-- Past Elections (Completed)
('Karnataka State Assembly Election 2024', 'STATE', '2024-05-10 08:00:00', '2024-05-15 18:00:00', 'COMPLETED', TRUE, '2024-05-16 10:00:00', 1, 1),
('Delhi Municipal Corporation Election 2024', 'LOCAL', '2024-03-01 08:00:00', '2024-03-05 18:00:00', 'COMPLETED', TRUE, '2024-03-06 10:00:00', 2, 2);

-- 4. INSERT 5 CANDIDATES FOR ELECTION 1 (Maharashtra State) AND 5 FOR ELECTION 2 (Mumbai Municipal)
-- Candidates for Election 1 (Maharashtra State Assembly Election 2025)
INSERT INTO candidates (user_id, election_id, party_name, party_symbol, manifesto) VALUES
(1, 1, 'Indian National Congress', 'Hand', 'Development for all sections of society, focus on education and healthcare reform'),
(3, 1, 'Bharatiya Janata Party', 'Lotus', 'Infrastructure development, digital transformation, and economic growth'),
(5, 1, 'Shiv Sena (UBT)', 'Flaming Torch', 'Protection of Marathi interests, industrial development, job creation'),
(7, 1, 'Nationalist Congress Party', 'Clock', 'Agricultural reforms, farmer welfare, rural development'),
(9, 1, 'Aam Aadmi Party', 'Broom', 'Anti-corruption measures, free utilities, quality education for all');

-- Candidates for Election 2 (Mumbai Municipal Corporation Election 2025)
INSERT INTO candidates (user_id, election_id, party_name, party_symbol, manifesto) VALUES
(2, 2, 'Indian National Congress', 'Hand', 'Better civic amenities, improved public transport, waste management'),
(4, 2, 'Bharatiya Janata Party', 'Lotus', 'Smart city initiatives, improved infrastructure, efficient administration'),
(6, 2, 'Shiv Sena (UBT)', 'Flaming Torch', 'Local employment priority, better housing, Mumbai for Mumbaikars'),
(8, 2, 'Nationalist Congress Party', 'Clock', 'Water supply improvement, road maintenance, green Mumbai initiative'),
(10, 2, 'Maharashtra Navnirman Sena', 'Railway Engine', 'Focus on middle-class welfare, affordable housing, traffic solutions');


-- 5. INSERT VOTES (17 users voted in Election 1, 18 users voted in Election 2)
-- Election 1 votes (Users 1-17 voted, Users 18-20 did NOT vote)
INSERT INTO votes (election_id, user_id, candidate_id, vote_hash, voted_at) VALUES
(1, 1, 1, CONCAT('hash_e1_u1_c1_', MD5(CONCAT('1', '1', '1', NOW()))), '2025-11-20 09:15:00'),
(1, 2, 2, CONCAT('hash_e1_u2_c2_', MD5(CONCAT('1', '2', '2', NOW()))), '2025-11-20 10:30:00'),
(1, 3, 3, CONCAT('hash_e1_u3_c3_', MD5(CONCAT('1', '3', '3', NOW()))), '2025-11-20 11:45:00'),
(1, 4, 1, CONCAT('hash_e1_u4_c1_', MD5(CONCAT('1', '4', '1', NOW()))), '2025-11-20 13:20:00'),
(1, 5, 2, CONCAT('hash_e1_u5_c2_', MD5(CONCAT('1', '5', '2', NOW()))), '2025-11-20 14:10:00'),
(1, 6, 4, CONCAT('hash_e1_u6_c4_', MD5(CONCAT('1', '6', '4', NOW()))), '2025-11-20 15:00:00'),
(1, 7, 5, CONCAT('hash_e1_u7_c5_', MD5(CONCAT('1', '7', '5', NOW()))), '2025-11-20 15:45:00'),
(1, 8, 1, CONCAT('hash_e1_u8_c1_', MD5(CONCAT('1', '8', '1', NOW()))), '2025-11-20 16:20:00'),
(1, 9, 2, CONCAT('hash_e1_u9_c2_', MD5(CONCAT('1', '9', '2', NOW()))), '2025-11-21 09:00:00'),
(1, 10, 3, CONCAT('hash_e1_u10_c3_', MD5(CONCAT('1', '10', '3', NOW()))), '2025-11-21 10:15:00'),
(1, 11, 4, CONCAT('hash_e1_u11_c4_', MD5(CONCAT('1', '11', '4', NOW()))), '2025-11-21 11:30:00'),
(1, 12, 1, CONCAT('hash_e1_u12_c1_', MD5(CONCAT('1', '12', '1', NOW()))), '2025-11-21 12:45:00'),
(1, 13, 2, CONCAT('hash_e1_u13_c2_', MD5(CONCAT('1', '13', '2', NOW()))), '2025-11-21 13:50:00'),
(1, 14, 5, CONCAT('hash_e1_u14_c5_', MD5(CONCAT('1', '14', '5', NOW()))), '2025-11-21 14:30:00'),
(1, 15, 3, CONCAT('hash_e1_u15_c3_', MD5(CONCAT('1', '15', '3', NOW()))), '2025-11-21 15:15:00'),
(1, 16, 1, CONCAT('hash_e1_u16_c1_', MD5(CONCAT('1', '16', '1', NOW()))), '2025-11-21 16:00:00'),
(1, 17, 2, CONCAT('hash_e1_u17_c2_', MD5(CONCAT('1', '17', '2', NOW()))), '2025-11-21 16:45:00');

-- Election 2 votes (Users 1-18 voted, Users 19-20 did NOT vote)
INSERT INTO votes (election_id, user_id, candidate_id, vote_hash, voted_at) VALUES
(2, 1, 6, CONCAT('hash_e2_u1_c6_', MD5(CONCAT('2', '1', '6', NOW()))), '2025-11-21 09:30:00'),
(2, 2, 7, CONCAT('hash_e2_u2_c7_', MD5(CONCAT('2', '2', '7', NOW()))), '2025-11-21 10:00:00'),
(2, 3, 8, CONCAT('hash_e2_u3_c8_', MD5(CONCAT('2', '3', '8', NOW()))), '2025-11-21 11:15:00'),
(2, 4, 6, CONCAT('hash_e2_u4_c6_', MD5(CONCAT('2', '4', '6', NOW()))), '2025-11-21 12:00:00'),
(2, 5, 7, CONCAT('hash_e2_u5_c7_', MD5(CONCAT('2', '5', '7', NOW()))), '2025-11-21 13:10:00'),
(2, 6, 9, CONCAT('hash_e2_u6_c9_', MD5(CONCAT('2', '6', '9', NOW()))), '2025-11-21 14:00:00'),
(2, 7, 10, CONCAT('hash_e2_u7_c10_', MD5(CONCAT('2', '7', '10', NOW()))), '2025-11-21 14:45:00'),
(2, 8, 6, CONCAT('hash_e2_u8_c6_', MD5(CONCAT('2', '8', '6', NOW()))), '2025-11-21 15:30:00'),
(2, 9, 7, CONCAT('hash_e2_u9_c7_', MD5(CONCAT('2', '9', '7', NOW()))), '2025-11-21 16:15:00'),
(2, 10, 8, CONCAT('hash_e2_u10_c8_', MD5(CONCAT('2', '10', '8', NOW()))), '2025-11-22 09:00:00'),
(2, 11, 9, CONCAT('hash_e2_u11_c9_', MD5(CONCAT('2', '11', '9', NOW()))), '2025-11-22 10:00:00'),
(2, 12, 6, CONCAT('hash_e2_u12_c6_', MD5(CONCAT('2', '12', '6', NOW()))), '2025-11-22 11:00:00'),
(2, 13, 7, CONCAT('hash_e2_u13_c7_', MD5(CONCAT('2', '13', '7', NOW()))), '2025-11-22 12:00:00'),
(2, 14, 10, CONCAT('hash_e2_u14_c10_', MD5(CONCAT('2', '14', '10', NOW()))), '2025-11-22 13:00:00'),
(2, 15, 8, CONCAT('hash_e2_u15_c8_', MD5(CONCAT('2', '15', '8', NOW()))), '2025-11-22 14:00:00'),
(2, 16, 6, CONCAT('hash_e2_u16_c6_', MD5(CONCAT('2', '16', '6', NOW()))), '2025-11-22 15:00:00'),
(2, 17, 7, CONCAT('hash_e2_u17_c7_', MD5(CONCAT('2', '17', '7', NOW()))), '2025-11-22 16:00:00'),
(2, 18, 8, CONCAT('hash_e2_u18_c8_', MD5(CONCAT('2', '18', '8', NOW()))), '2025-11-22 16:30:00');


-- UPDATE voter_election_status for all users in both active elections
-- Election 1: Users 1-17 voted, Users 18-20 did NOT vote
INSERT INTO voter_election_status (election_id, user_id, has_voted, voted_at) VALUES
(1, 1, TRUE, '2025-11-20 09:15:00'),
(1, 2, TRUE, '2025-11-20 10:30:00'),
(1, 3, TRUE, '2025-11-20 11:45:00'),
(1, 4, TRUE, '2025-11-20 13:20:00'),
(1, 5, TRUE, '2025-11-20 14:10:00'),
(1, 6, TRUE, '2025-11-20 15:00:00'),
(1, 7, TRUE, '2025-11-20 15:45:00'),
(1, 8, TRUE, '2025-11-20 16:20:00'),
(1, 9, TRUE, '2025-11-21 09:00:00'),
(1, 10, TRUE, '2025-11-21 10:15:00'),
(1, 11, TRUE, '2025-11-21 11:30:00'),
(1, 12, TRUE, '2025-11-21 12:45:00'),
(1, 13, TRUE, '2025-11-21 13:50:00'),
(1, 14, TRUE, '2025-11-21 14:30:00'),
(1, 15, TRUE, '2025-11-21 15:15:00'),
(1, 16, TRUE, '2025-11-21 16:00:00'),
(1, 17, TRUE, '2025-11-21 16:45:00'),
(1, 18, FALSE, NULL),
(1, 19, FALSE, NULL),
(1, 20, FALSE, NULL);

-- Election 2: Users 1-18 voted, Users 19-20 did NOT vote
INSERT INTO voter_election_status (election_id, user_id, has_voted, voted_at) VALUES
(2, 1, TRUE, '2025-11-21 09:30:00'),
(2, 2, TRUE, '2025-11-21 10:00:00'),
(2, 3, TRUE, '2025-11-21 11:15:00'),
(2, 4, TRUE, '2025-11-21 12:00:00'),
(2, 5, TRUE, '2025-11-21 13:10:00'),
(2, 6, TRUE, '2025-11-21 14:00:00'),
(2, 7, TRUE, '2025-11-21 14:45:00'),
(2, 8, TRUE, '2025-11-21 15:30:00'),
(2, 9, TRUE, '2025-11-21 16:15:00'),
(2, 10, TRUE, '2025-11-22 09:00:00'),
(2, 11, TRUE, '2025-11-22 10:00:00'),
(2, 12, TRUE, '2025-11-22 11:00:00'),
(2, 13, TRUE, '2025-11-22 12:00:00'),
(2, 14, TRUE, '2025-11-22 13:00:00'),
(2, 15, TRUE, '2025-11-22 14:00:00'),
(2, 16, TRUE, '2025-11-22 15:00:00'),
(2, 17, TRUE, '2025-11-22 16:00:00'),
(2, 18, TRUE, '2025-11-22 16:30:00'),
(2, 19, FALSE, NULL),
(2, 20, FALSE, NULL);

-- 6. UPDATE election_results table (for active elections - current vote counts)
-- Election 1 Results (Maharashtra State - 17 votes cast)
-- Candidate 1 (user_id 1): 5 votes
-- Candidate 2 (user_id 3): 6 votes
-- Candidate 3 (user_id 5): 3 votes
-- Candidate 4 (user_id 7): 2 votes
-- Candidate 5 (user_id 9): 1 vote
INSERT INTO election_results (election_id, candidate_id, vote_count, vote_percentage, rank_position) VALUES
(1, 1, 5, 29.41, 2),
(1, 2, 6, 35.29, 1),
(1, 3, 3, 17.65, 3),
(1, 4, 2, 11.76, 4),
(1, 5, 1, 5.88, 5);

-- Election 2 Results (Mumbai Municipal - 18 votes cast)
-- Candidate 6 (user_id 2): 6 votes
-- Candidate 7 (user_id 4): 6 votes
-- Candidate 8 (user_id 6): 4 votes
-- Candidate 9 (user_id 8): 1 vote
-- Candidate 10 (user_id 10): 1 vote
INSERT INTO election_results (election_id, candidate_id, vote_count, vote_percentage, rank_position) VALUES
(2, 6, 6, 33.33, 1),
(2, 7, 6, 33.33, 1),
(2, 8, 4, 22.22, 3),
(2, 9, 1, 5.56, 4),
(2, 10, 1, 5.56, 4);

-- UPDATE election_reports table
-- Report for Election 1 (Maharashtra State Assembly)
INSERT INTO election_reports (election_id, total_registered_voters, total_votes_cast, voter_turnout_percentage, total_candidates, winning_candidate_id, winning_margin, report_generated_by) VALUES
(1, 20, 17, 85.00, 5, 2, 1, 1);

-- Report for Election 2 (Mumbai Municipal Corporation)
INSERT INTO election_reports (election_id, total_registered_voters, total_votes_cast, voter_turnout_percentage, total_candidates, winning_candidate_id, winning_margin, report_generated_by) VALUES
(2, 20, 18, 90.00, 5, 6, 0, 2);


-- 7. NEW USERS AND LOCATION UPDATES (January 20, 2026)
-- Update Existing Elections with specific locations
UPDATE elections SET state = 'Maharashtra', election_type = 'STATE' WHERE election_name = 'Maharashtra State Assembly Election 2025';
UPDATE elections SET city = 'Mumbai', state = 'Maharashtra', election_type = 'LOCAL' WHERE election_name = 'Mumbai Municipal Corporation Election 2025';
UPDATE elections SET state = 'Karnataka', election_type = 'STATE' WHERE election_name = 'Karnataka State Assembly Election 2024';
UPDATE elections SET city = 'Delhi', state = 'Delhi', election_type = 'LOCAL' WHERE election_name = 'Delhi Municipal Corporation Election 2024';

-- ============================================
-- MISSING DATA FOR KARNATAKA AND DELHI ELECTIONS
-- ============================================

-- 4. INSERT CANDIDATES FOR ELECTION 3 (Karnataka) AND ELECTION 4 (Delhi)
-- Candidates for Election 3 (Karnataka State Assembly Election 2024)
-- IDs will be 11, 12, 13, 14, 15
INSERT INTO candidates (user_id, election_id, party_name, party_symbol, manifesto) VALUES
(11, 3, 'Bharatiya Janata Party', 'Lotus', 'Focus on urban infrastructure and tech growth'),
(12, 3, 'Indian National Congress', 'Hand', 'Welfare schemes for women and youth'),
(13, 3, 'Janata Dal (Secular)', 'Woman carrying paddy sheaf', 'Farmer support and rural development'),
(14, 3, 'Aam Aadmi Party', 'Broom', 'Quality education and healthcare for all'),
(15, 3, 'Independent', 'Kite', 'Voice for the local community issues');

-- Candidates for Election 4 (Delhi Municipal Corporation Election 2024)
-- IDs will be 16, 17, 18, 19, 20
INSERT INTO candidates (user_id, election_id, party_name, party_symbol, manifesto) VALUES
(16, 4, 'Aam Aadmi Party', 'Broom', 'Clean Delhi, Green Delhi initiative'),
(17, 4, 'Bharatiya Janata Party', 'Lotus', 'Efficient waste management and civic amenities'),
(18, 4, 'Indian National Congress', 'Hand', 'Empowering local governance bodies'),
(19, 4, 'Independent', 'Bat', 'Traffic decongestion and road safety'),
(20, 4, 'Bahujan Samaj Party', 'Elephant', 'Social justice and equal rights');

-- 5. INSERT VOTES
-- Election 3 votes (Users 1-15 voted)
INSERT INTO votes (election_id, user_id, candidate_id, vote_hash, voted_at) VALUES
(3, 1, 11, CONCAT('hash_e3_u1_c11_', MD5(CONCAT('3', '1', '11', NOW()))), '2024-05-10 09:00:00'),
(3, 2, 12, CONCAT('hash_e3_u2_c12_', MD5(CONCAT('3', '2', '12', NOW()))), '2024-05-10 09:30:00'),
(3, 3, 11, CONCAT('hash_e3_u3_c11_', MD5(CONCAT('3', '3', '11', NOW()))), '2024-05-10 10:00:00'),
(3, 4, 13, CONCAT('hash_e3_u4_c13_', MD5(CONCAT('3', '4', '13', NOW()))), '2024-05-10 10:45:00'),
(3, 5, 11, CONCAT('hash_e3_u5_c11_', MD5(CONCAT('3', '5', '11', NOW()))), '2024-05-10 11:15:00'),
(3, 6, 12, CONCAT('hash_e3_u6_c12_', MD5(CONCAT('3', '6', '12', NOW()))), '2024-05-10 12:00:00'),
(3, 7, 14, CONCAT('hash_e3_u7_c14_', MD5(CONCAT('3', '7', '14', NOW()))), '2024-05-10 12:45:00'),
(3, 8, 11, CONCAT('hash_e3_u8_c11_', MD5(CONCAT('3', '8', '11', NOW()))), '2024-05-10 13:30:00'),
(3, 9, 15, CONCAT('hash_e3_u9_c15_', MD5(CONCAT('3', '9', '15', NOW()))), '2024-05-10 14:00:00'),
(3, 10, 11, CONCAT('hash_e3_u10_c11_', MD5(CONCAT('3', '10', '11', NOW()))), '2024-05-10 15:00:00'),
(3, 11, 12, CONCAT('hash_e3_u11_c12_', MD5(CONCAT('3', '11', '12', NOW()))), '2024-05-11 09:00:00'),
(3, 12, 11, CONCAT('hash_e3_u12_c11_', MD5(CONCAT('3', '12', '11', NOW()))), '2024-05-11 10:00:00'),
(3, 13, 13, CONCAT('hash_e3_u13_c13_', MD5(CONCAT('3', '13', '13', NOW()))), '2024-05-11 11:00:00'),
(3, 14, 11, CONCAT('hash_e3_u14_c11_', MD5(CONCAT('3', '14', '11', NOW()))), '2024-05-11 12:00:00'),
(3, 15, 12, CONCAT('hash_e3_u15_c12_', MD5(CONCAT('3', '15', '12', NOW()))), '2024-05-11 13:00:00');

-- Election 4 votes (Users 1-16 voted)
INSERT INTO votes (election_id, user_id, candidate_id, vote_hash, voted_at) VALUES
(4, 1, 16, CONCAT('hash_e4_u1_c16_', MD5(CONCAT('4', '1', '16', NOW()))), '2024-03-01 09:00:00'),
(4, 2, 17, CONCAT('hash_e4_u2_c17_', MD5(CONCAT('4', '2', '17', NOW()))), '2024-03-01 09:45:00'),
(4, 3, 16, CONCAT('hash_e4_u3_c16_', MD5(CONCAT('4', '3', '16', NOW()))), '2024-03-01 10:15:00'),
(4, 4, 18, CONCAT('hash_e4_u4_c18_', MD5(CONCAT('4', '4', '18', NOW()))), '2024-03-01 11:00:00'),
(4, 5, 16, CONCAT('hash_e4_u5_c16_', MD5(CONCAT('4', '5', '16', NOW()))), '2024-03-01 11:45:00'),
(4, 6, 19, CONCAT('hash_e4_u6_c19_', MD5(CONCAT('4', '6', '19', NOW()))), '2024-03-01 12:30:00'),
(4, 7, 16, CONCAT('hash_e4_u7_c16_', MD5(CONCAT('4', '7', '16', NOW()))), '2024-03-01 13:15:00'),
(4, 8, 17, CONCAT('hash_e4_u8_c17_', MD5(CONCAT('4', '8', '17', NOW()))), '2024-03-01 14:00:00'),
(4, 9, 16, CONCAT('hash_e4_u9_c16_', MD5(CONCAT('4', '9', '16', NOW()))), '2024-03-01 15:00:00'),
(4, 10, 20, CONCAT('hash_e4_u10_c20_', MD5(CONCAT('4', '10', '20', NOW()))), '2024-03-01 16:00:00'),
(4, 11, 16, CONCAT('hash_e4_u11_c16_', MD5(CONCAT('4', '11', '16', NOW()))), '2024-03-02 09:00:00'),
(4, 12, 17, CONCAT('hash_e4_u12_c17_', MD5(CONCAT('4', '12', '17', NOW()))), '2024-03-02 10:00:00'),
(4, 13, 16, CONCAT('hash_e4_u13_c16_', MD5(CONCAT('4', '13', '16', NOW()))), '2024-03-02 11:00:00'),
(4, 14, 18, CONCAT('hash_e4_u14_c18_', MD5(CONCAT('4', '14', '18', NOW()))), '2024-03-02 12:00:00'),
(4, 15, 16, CONCAT('hash_e4_u15_c16_', MD5(CONCAT('4', '15', '16', NOW()))), '2024-03-02 13:00:00'),
(4, 16, 17, CONCAT('hash_e4_u16_c17_', MD5(CONCAT('4', '16', '17', NOW()))), '2024-03-02 14:00:00');

-- 6. UPDATE voter_election_status
-- Election 3: Users 1-15 voted, 16-20 did not
INSERT INTO voter_election_status (election_id, user_id, has_voted, voted_at) VALUES
(3, 1, TRUE, '2024-05-10 09:00:00'),
(3, 2, TRUE, '2024-05-10 09:30:00'),
(3, 3, TRUE, '2024-05-10 10:00:00'),
(3, 4, TRUE, '2024-05-10 10:45:00'),
(3, 5, TRUE, '2024-05-10 11:15:00'),
(3, 6, TRUE, '2024-05-10 12:00:00'),
(3, 7, TRUE, '2024-05-10 12:45:00'),
(3, 8, TRUE, '2024-05-10 13:30:00'),
(3, 9, TRUE, '2024-05-10 14:00:00'),
(3, 10, TRUE, '2024-05-10 15:00:00'),
(3, 11, TRUE, '2024-05-11 09:00:00'),
(3, 12, TRUE, '2024-05-11 10:00:00'),
(3, 13, TRUE, '2024-05-11 11:00:00'),
(3, 14, TRUE, '2024-05-11 12:00:00'),
(3, 15, TRUE, '2024-05-11 13:00:00'),
(3, 16, FALSE, NULL),
(3, 17, FALSE, NULL),
(3, 18, FALSE, NULL),
(3, 19, FALSE, NULL),
(3, 20, FALSE, NULL);

-- Election 4: Users 1-16 voted, 17-20 did not
INSERT INTO voter_election_status (election_id, user_id, has_voted, voted_at) VALUES
(4, 1, TRUE, '2024-03-01 09:00:00'),
(4, 2, TRUE, '2024-03-01 09:45:00'),
(4, 3, TRUE, '2024-03-01 10:15:00'),
(4, 4, TRUE, '2024-03-01 11:00:00'),
(4, 5, TRUE, '2024-03-01 11:45:00'),
(4, 6, TRUE, '2024-03-01 12:30:00'),
(4, 7, TRUE, '2024-03-01 13:15:00'),
(4, 8, TRUE, '2024-03-01 14:00:00'),
(4, 9, TRUE, '2024-03-01 15:00:00'),
(4, 10, TRUE, '2024-03-01 16:00:00'),
(4, 11, TRUE, '2024-03-02 09:00:00'),
(4, 12, TRUE, '2024-03-02 10:00:00'),
(4, 13, TRUE, '2024-03-02 11:00:00'),
(4, 14, TRUE, '2024-03-02 12:00:00'),
(4, 15, TRUE, '2024-03-02 13:00:00'),
(4, 16, TRUE, '2024-03-02 14:00:00'),
(4, 17, FALSE, NULL),
(4, 18, FALSE, NULL),
(4, 19, FALSE, NULL),
(4, 20, FALSE, NULL);

-- 7. UPDATE election_results
-- Election 3 Results (Karnataka - 15 votes)
-- Candidate 11: 7 votes (Users 1,3,5,8,10,12,14)
-- Candidate 12: 4 votes (Users 2,6,11,15)
-- Candidate 13: 2 votes (Users 4,13)
-- Candidate 14: 1 vote (User 7)
-- Candidate 15: 1 vote (User 9)
INSERT INTO election_results (election_id, candidate_id, vote_count, vote_percentage, rank_position) VALUES
(3, 11, 7, 46.67, 1),
(3, 12, 4, 26.67, 2),
(3, 13, 2, 13.33, 3),
(3, 14, 1, 6.67, 4),
(3, 15, 1, 6.67, 4);

-- Election 4 Results (Delhi - 16 votes)
-- Candidate 16: 8 votes (Users 1,3,5,7,9,11,13,15)
-- Candidate 17: 4 votes (Users 2,8,12,16)
-- Candidate 18: 2 votes (Users 4,14)
-- Candidate 19: 1 vote (User 6)
-- Candidate 20: 1 vote (User 10)
INSERT INTO election_results (election_id, candidate_id, vote_count, vote_percentage, rank_position) VALUES
(4, 16, 8, 50.00, 1),
(4, 17, 4, 25.00, 2),
(4, 18, 2, 12.50, 3),
(4, 19, 1, 6.25, 4),
(4, 20, 1, 6.25, 4);

-- UPDATE election_reports
-- Report for Election 3 (Karnataka)
INSERT INTO election_reports (election_id, total_registered_voters, total_votes_cast, voter_turnout_percentage, total_candidates, winning_candidate_id, winning_margin, report_generated_by) VALUES
(3, 20, 15, 75.00, 5, 11, 3, 1);

-- Report for Election 4 (Delhi)
INSERT INTO election_reports (election_id, total_registered_voters, total_votes_cast, voter_turnout_percentage, total_candidates, winning_candidate_id, winning_margin, report_generated_by) VALUES
(4, 20, 16, 80.00, 5, 16, 4, 2);

-- ============================================
-- NEW REGIONAL ELECTIONS (UP, MP, INDORE)
-- ============================================

-- 1. INSERT NEW ACTIVE ELECTIONS
INSERT INTO elections (election_name, election_type, start_date, end_date, city, state, status, result_published, created_by) VALUES
('Uttar Pradesh State Assembly Election 2026', 'STATE', '2026-02-01 08:00:00', '2026-02-10 18:00:00', NULL, 'Uttar Pradesh', 'ACTIVE', FALSE, 1),
('Madhya Pradesh State Assembly Election 2026', 'STATE', '2026-02-05 08:00:00', '2026-02-15 18:00:00', NULL, 'Madhya Pradesh', 'ACTIVE', FALSE, 1),
('Indore Municipal Corporation Election 2026', 'LOCAL', '2026-02-05 08:00:00', '2026-02-08 18:00:00', 'Indore', 'Madhya Pradesh', 'ACTIVE', FALSE, 2);

-- Note: IDs are likely 5, 6, 7 respectively assuming previous were 1-4.
-- Storing IDs in variables is not supported in standard SQL scripts usually, so we assume 5, 6, 7.

-- 2. INSERT CANDIDATES
-- For UP (Election ID 5)
INSERT INTO candidates (user_id, election_id, party_name, party_symbol, manifesto) VALUES
(21, 5, 'Bharatiya Janata Party', 'Lotus', 'Development for UP'), -- Madhur Chaudhari (Actually from Jalgaon/Pune in list but using for demo or picking distinct users)
-- Wait, let's pick users who are NOT likely to vote in these elections or just valid generic users. 
-- The Candidates table references user_id. Candidate users don't have to be from that region necessarily (though usually are).
-- Let's use users 21, 22, 23 for UP candidates (assuming they are candidates).
(35, 5, 'Samajwadi Party', 'Bicycle', 'Social Justice'),
(22, 5, 'Bahujan Samaj Party', 'Elephant', 'Inclusive Growth'),
(23, 5, 'Indian National Congress', 'Hand', 'Change for Progress');

-- For MP (Election ID 6)
INSERT INTO candidates (user_id, election_id, party_name, party_symbol, manifesto) VALUES
(24, 6, 'Bharatiya Janata Party', 'Lotus', 'MP ke Man me Modi'),
(25, 6, 'Indian National Congress', 'Hand', 'Welfare for MP'),
(26, 6, 'Aam Aadmi Party', 'Broom', 'Clean Politics');

-- For Indore (Election ID 7)
INSERT INTO candidates (user_id, election_id, party_name, party_symbol, manifesto) VALUES
(27, 7, 'Bharatiya Janata Party', 'Lotus', 'Cleanest City Forever'),
(28, 7, 'Indian National Congress', 'Hand', 'Better Infrastructure'),
(29, 7, 'Independent', 'Bat', 'Local Voice');

-- 3. INSERT VOTES (Simulate some voting)
-- UP Voters (State = Uttar Pradesh)
-- Users: Anuj Tomar (Agra), Rajat Morya (Bareilly), Ayush Kush (Lucknow), etc.
-- Let's assume User IDs for them.
-- From the script: 
-- Anuj Tomar -> ID 22 (created in '7. NEW USERS...' block)
-- Rajat Morya -> ID 26
-- Ayush Kush -> ID 27
-- Prabal Singh -> ID 36
-- Anuj Sharma -> ID 41
-- Apoorv Singh -> ID 49
-- Pranav Tiwari -> ID 50
-- Plus the bulk users starting from ID 51...
-- Let's use subqueries to be safe or just assume IDs based on the insert order in finalschema.sql

-- Voting in UP Election (ID 5)
-- Anuj Tomar (22) votes for Candidate 1 (User 21 - ID in candidates table is auto-inc, let's say 21)
-- Candidate IDs:
-- UP: 21, 22, 23 -> Candidate IDs 21, 22, 23
-- MP: 24, 25, 26 -> Candidate IDs 24, 25, 26
-- Indore: 27, 28, 29 -> Candidate IDs 27, 28, 29
-- (Assuming continuous auto-inc from 20)

INSERT INTO votes (election_id, user_id, candidate_id, vote_hash, voted_at) VALUES
(5, 22, 21, CONCAT('hash_e5_u22_', MD5(NOW())), NOW()), -- Anuj Tomar votes
(5, 26, 22, CONCAT('hash_e5_u26_', MD5(NOW())), NOW()), -- Rajat Morya votes
(5, 27, 21, CONCAT('hash_e5_u27_', MD5(NOW())), NOW()); -- Ayush Kush votes

INSERT INTO voter_election_status (election_id, user_id, has_voted, voted_at) VALUES
(5, 22, TRUE, NOW()),
(5, 26, TRUE, NOW()),
(5, 27, TRUE, NOW()),
(5, 36, FALSE, NULL), -- Prabal Singh (Not voted)
(5, 41, FALSE, NULL); -- Anuj Sharma (Not voted)

-- Voting in MP Election (ID 6)
-- Users: Satyam Patel (Katni - 23), Satyam Bavankar (Seoni - 30), Ansh Mittal (Gwalior - 33), Rajdeep Kala (Ujjain - 34)
INSERT INTO votes (election_id, user_id, candidate_id, vote_hash, voted_at) VALUES
(6, 23, 24, CONCAT('hash_e6_u23_', MD5(NOW())), NOW()), -- Satyam Patel votes
(6, 30, 25, CONCAT('hash_e6_u30_', MD5(NOW())), NOW()); -- Satyam Bavankar votes

INSERT INTO voter_election_status (election_id, user_id, has_voted, voted_at) VALUES
(6, 23, TRUE, NOW()),
(6, 30, TRUE, NOW()),
(6, 33, FALSE, NULL), -- Ansh Mittal
(6, 34, FALSE, NULL); -- Rajdeep Kala

-- Voting in Indore Election (ID 7) - LOCAL
-- Users: Pooja Joshi (Indore - 12), Himanshu Muleva (40), Abhijeet Singh (51)
-- Also generic users: Abhijit Mandloi (Indore), Ansh Jain, etc.
INSERT INTO votes (election_id, user_id, candidate_id, vote_hash, voted_at) VALUES
(7, 12, 27, CONCAT('hash_e7_u12_', MD5(NOW())), NOW()), -- Pooja Joshi votes
(7, 40, 27, CONCAT('hash_e7_u40_', MD5(NOW())), NOW()); -- Himanshu Muleva votes

INSERT INTO voter_election_status (election_id, user_id, has_voted, voted_at) VALUES
(7, 12, TRUE, NOW()),
(7, 40, TRUE, NOW()),
(7, 51, FALSE, NULL); -- Abhijeet Singh



```
