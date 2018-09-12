INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('hotel.wired.superwired.info', 'Superwired Usage Information. Possible reward types:<br/>badge: BADGE CODE<br/>Credits: credits#amount</br>Pixels: pixels#amount</br>Points: points#amount</br>Respect: respect#amount<br/>Furniture: furni#FurnitureID<br/>Catalog Item: cata#CatalogItemID<br/>');
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.calendar.enabled', '0');

ALTER TABLE  `permissions` ADD  `acc_inifnite_friends` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '0';

INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('hotel.wired.kickexception.unkickable', 'Wired Kick Exception: Unkickable'), ('hotel.wired.kickexception.owner', 'Wired Kick Exception: Room Owner');

INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('images.gamecenter.snowwar', 'c_images/gamecenter_snowwar/'), ('images.gamecenter.basejump', 'c_images/gamecenter_basejump/');

INSERT INTO `emulator_settings` (`key`, `value`) VALUES
 ('hotel.purchase.ltd.limit.daily.total', '10'), #Amount of LTDs you can buy per day.
 ('hotel.purchase.ltd.limit.daily.item', '3'); #Amount of LTDs you can buy of a specific item per day.

CREATE INDEX user_timestamp_index ON catalog_items_limited (user_id, timestamp);

INSERT INTO `emulator_texts` (`key`, `value`) VALUES
    ('error.catalog.buy.limited.daily.item', 'You cannot purchase any limited %itemname% for today as you have reached the limt of %limit%. Come back tomorrow.'),
    ('error.catalog.buy.limited.daily.total', 'You cannot purchase any limited furniture as you have reached the limited of %limit% for today. Come back tomorrow.');

INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.catalog.ltd.limit.enabled', '1');

INSERT INTO `emulator_settings` (`key` ,`value`) VALUES ('hotel.welcome.alert.oldstyle',  '0'); #Set to 1 to use the old Black MOTD Style (Same as commands window)

INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.welcome.alert.delay', '10000');

INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.calendar.starttimestamp', '1512238152');

ALTER TABLE `permissions` ADD `cmd_calendar` ENUM('0','1') NOT NULL DEFAULT '0' AFTER `cmd_bundle`;

INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.keys.cmd_calendar', 'calendar'), ('commands.description.cmd_calendar', ':calendar');

ALTER TABLE `pet_actions` CHANGE `pet_name` `pet_name` VARCHAR(32) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT '';
ALTER TABLE `pet_actions` CHANGE `happy_actions` `happy_actions` VARCHAR(100) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT '';
ALTER TABLE `pet_actions` CHANGE `tired_actions` `tired_actions` VARCHAR(100) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT '', CHANGE `random_actions` `random_actions` VARCHAR(100) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT '';