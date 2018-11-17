#DATABASE UPDATE: 1.13.0 -> 1.14.0

#Defines if you are sorting the catalog items using the catalog_items.order_number column or using the regular IDs.
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.catalog.items.display.ordernum', '0');

#Enables / Disables the talenttrack. If set to false, trading does not require the perk (even if 'hotel.trading.requires.perk' is set to 1)
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.talenttrack.enabled', '1');

#Sort using the navigator_flatcats and navigator_publiccats order_num If false use activity as sorting.
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.navigator.sort.ordernum', '1');

ALTER TABLE `catalog_items` ADD `order_number` TINYINT(2) NOT NULL DEFAULT '0' AFTER `offer_id`;

ALTER TABLE `permissions` CHANGE `acc_inifnite_friends` `acc_infinite_friends` ENUM('0','1') CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0';

UPDATE `emulator_texts` SET `value` = 'Superwired Usage Information. Possible reward types:<br/>badge: BADGE CODE<br/>Credits: credits#amount<br/>Pixels: pixels#amount<br/>Points: points#amount<br/>Respect: respect#amount<br/>Furniture: furni#FurnitureID<br/>Catalog Item: cata#CatalogItemID<br/>' WHERE `emulator_texts`.`key` = 'hotel.wired.superwired.info';
ALTER TABLE `navigator_publiccats` ADD `order_num` INT(3) NOT NULL DEFAULT '0' AFTER `visible`;
ALTER TABLE `navigator_flatcats` ADD `order_num` INT(3) NOT NULL DEFAULT '0' AFTER `list_type`;

INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.wordfilter.automute', '1');
#DATABASE UPDATE: 1.13.0 -> 1.14.0