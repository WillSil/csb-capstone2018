-- User Roles
INSERT INTO user_role_model(user_role_type, user_role_description, user_role_priority) VALUES ('administrator', 'Administrator', 1000) ON CONFLICT DO NOTHING;
INSERT INTO user_role_model(user_role_type, user_role_description, user_role_priority) VALUES ('legal_contractor', 'Legal Contract', 200) ON CONFLICT DO NOTHING;
INSERT INTO user_role_model(user_role_type, user_role_description, user_role_priority) VALUES ('fiscal_manager', 'Fiscal Manager', 100) ON CONFLICT DO NOTHING;

-- Business Parent Status
INSERT INTO business_status_parent_model (status_parent_type, status_parent_description, phase_number, parent_order_number) VALUES ('dummy_value', 'Ignore this', 1, 1) ON CONFLICT DO NOTHING;

-- Business Child Status
INSERT INTO business_status_child_model (status_child_type, status_parent_type, status_child_description, child_order_number) VALUES ('phase_1', 'dummy_value', 'Phase 1: Initial Contact', 1) ON CONFLICT DO NOTHING;
INSERT INTO business_status_child_model (status_child_type, status_parent_type, status_child_description, child_order_number) VALUES ('phase_2', 'dummy_value', 'Phase 2: Formal Interest', 2) ON CONFLICT DO NOTHING;
INSERT INTO business_status_child_model (status_child_type, status_parent_type, status_child_description, child_order_number) VALUES ('phase_3', 'dummy_value', 'Phase 3: Project Defined', 3) ON CONFLICT DO NOTHING;
INSERT INTO business_status_child_model (status_child_type, status_parent_type, status_child_description, child_order_number) VALUES ('phase_4', 'dummy_value', 'Phase 4: Price Finalized', 4) ON CONFLICT DO NOTHING;
INSERT INTO business_status_child_model (status_child_type, status_parent_type, status_child_description, child_order_number) VALUES ('phase_5', 'dummy_value', 'Phase 5: Contract Approved', 5) ON CONFLICT DO NOTHING;
INSERT INTO business_status_child_model (status_child_type, status_parent_type, status_child_description, child_order_number) VALUES ('phase_6', 'dummy_value', 'Phase 6: Contract Signed', 6) ON CONFLICT DO NOTHING;
INSERT INTO business_status_child_model (status_child_type, status_parent_type, status_child_description, child_order_number) VALUES ('phase_7', 'dummy_value', 'Phase 7: Funds Received', 7) ON CONFLICT DO NOTHING;
INSERT INTO business_status_child_model (status_child_type, status_parent_type, status_child_description, child_order_number) VALUES ('phase_8', 'dummy_value', 'Phase 8: Fully Engaged', 8) ON CONFLICT DO NOTHING;