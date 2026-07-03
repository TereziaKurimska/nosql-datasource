CREATE USER IF NOT EXISTS `ais-like-user`@`localhost` IDENTIFIED BY 'iceIceBaby';
CREATE USER IF NOT EXISTS `ais-like-user`@`%` IDENTIFIED BY 'iceIceBaby';
GRANT ALL PRIVILEGES ON `ais-like`.* TO `ais-like-user`@`localhost`;
GRANT ALL PRIVILEGES ON `ais-like`.* TO `ais-like-user`@`%`;
FLUSH PRIVILEGES;
