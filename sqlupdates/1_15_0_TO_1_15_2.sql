CREATE INDEX room_enter_log_room_id ON room_enter_log (room_id);
CREATE INDEX room_enter_log_user_entry ON room_enter_log (user_id, timestamp);
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.messenger.search.maxresults', '50');
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.alert.oldstyle', '0');
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.navigator.staffpicks.categoryid', '1'); #NOTE THIS IS navigator_publiccats