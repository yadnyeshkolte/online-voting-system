---
title: "Database Insertion Queries 2"
permalink: /docs/database/databaseinsertion2/
excerpt: "Inserting Data into Database 2"
last_modified_at: 2025-11-24
toc: true
---

```sql



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

```