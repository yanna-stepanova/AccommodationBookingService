insert into roles values
(1, 'ADMIN'),
(2, 'CUSTOMER');
insert into users values
(1, 'admin@gmail.com', 'Admin', 'Current', 'admin1234', 1),
(2, 'user@gmail.com', 'Username', 'UserSurname', 'user1234', 2);
insert into amenities values
(1, 'WiFi', 'WiFi is free'),
(2, 'Parking(secured)', 'Private parking'),
(3, 'Hairdryer', null),
(4, 'Swimming pool', 'Indoor pool with sauna and Finnish bath'),
(5, 'Parking(unsecured)', 'Free parking');
insert into locations values
(1, 'Ukraine', 'Novy Yar', 'Lviv region', '81050', 'Yavorian Lake, SIRKA SPORT', 'cell: +38(073)8761234, https://sirka.ua, Google Maps(49.951422, 23.488195)'),
(2, 'Ukraine', 'Skhidnytsia', 'Lviv region', '82391', 'Boryslavska Street, 81', 'cell: +38(097)7715102, https://www.lubo-kray.com.ua');
insert into accommodations values
(1, 'VACATION_HOME', 1, '1 Bedroom', 100.05, 2),
(2, 'HOTEL', 2, 'apartment-room', 98.05, 5),
(3, 'HOTEL', 2, 'mansard-room', 80.05, 8);
insert into accommodations_amenities values
(1, 1),
(1, 2),
(1, 3),
(2, 1),
(2, 3),
(2, 4),
(2, 5),
(3, 1),
(3, 3),
(3, 4),
(3, 5);
