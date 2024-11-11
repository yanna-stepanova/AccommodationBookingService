insert into roles values
(1, 'ADMIN'),
(2, 'CUSTOMER');
insert into users values
(1, 'admin@gmail.com', 'Admin', 'Current', 'admin1234', 1),
(2, 'user@gmail.com', 'Username', 'UserSurname', 'user1234', 2);
insert into locations values
(1, 'Ukraine', 'Novy Yar', 'Lviv region', '81050', 'Yavorian Lake, SIRKA SPORT', 'cell: +38(073)8761234, https://sirka.ua, Google Maps(49.951422, 23.488195)'),
(2, 'Ukraine', 'Skhidnytsia', 'Lviv region', '82391', 'Boryslavska Street, 81', 'cell: +38(097)7715102, https://www.lubo-kray.com.ua');
insert into amenities values
(1, 'WiFi', 'WiFi is free'),
(2, 'Parking(secured)', 'Private parking'),
(3, 'Hairdryer', null),
(4, 'Swimming pool', 'Indoor pool with sauna and Finnish bath'),
(5, 'Parking(unsecured)', 'Free parking');
insert into accommodations values
(1, 'VACATION_HOME', 1, '1 Bedroom', 100, 2),
(2, 'HOTEL', 2, 'apartment-room', 98, 5),
(3, 'HOTEL', 2, 'mansard-room', 80, 8);
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
insert into bookings values
(1, '2024-11-13', '2024-11-14', 1, 2, 'PAID'),
(2, '2024-11-13', '2024-11-14', 2, 2, 'PENDING'),
(3, '2024-11-13', '2024-11-16', 1, 2, 'CANCELED');
insert into payments values
(1, 'PAID', '2024-11-14 01:26:59.789666', 1, 100, 'https://checkout.stripe.com/c/pay/cs_test_a11G0LTvDQ5zGNTskpvipghM01gQGLrS7B5B9RVchXD6PlmH5sBfuq4Kou#fidkdWxOYHwnPyd1blpxYHZxWjA0VDxEQnZXcndCbGMzUXNsRE1PNVxdVDczc39sZFQwZ3JnY0NHfENhamlfQEZWNVY9bVdrdjM9QWFdU0F9NkF1fHx2U0hEb0JSQUpBSnJuQV9iSW5CbjdXNTVzbXBsNlNqcCcpJ2N3amhWYHdzYHcnP3F3cGApJ2lkfGpwcVF8dWAnPyd2bGtiaWBabHFgaCcpJ2BrZGdpYFVpZGZgbWppYWB3dic%2FcXdwYHgl', 'cs_test_a11G0LTvDQ5zGNTskpvipghM01gQGLrS7B5B9RVchXD6PlmH5sBfuq4Kou'),
(2, 'PENDING', '2024-11-14 14:48:35.584967', 2, 98, 'https://checkout.stripe.com/c/pay/cs_test_a13G0LTvDQ5zGNTskpvipghM01gQGLrS7B5B9RVchXD6PlmH5sBfuq4Kou#fidkdWxOYHwnPyd1blpxYHZxWjA0VDxEQnZXcndCbGMzUXNsRE1PNVxdVDczc39sZFQwZ3JnY0NHfENhamlfQEZWNVY9bVdrdjM9QWFdU0F9NkF1fHx2U0hEb0JSQUpBSnJuQV9iSW5CbjdXNTVzbXBsNlNqcCcpJ2N3amhWYHdzYHcnP3F3cGApJ2lkfGpwcVF8dWAnPyd2bGtiaWBabHFgaCcpJ2BrZGdpYFVpZGZgbWppYWB3dic%3FcXdwYHgd', 'cs_test_a3gb3y8A2jIUfj3rWUr6hIHtDUkKy78Ab48NHhDBZ8J5010f1GOKbwjTsT'),
(3, 'CANCELED', '2024-11-13 10:05:17.123456', 3, 100, 'https://checkout.stripe.com/c/pay/cs_test_a12G0LTvDQ5zGNTskpvipghM01gQGLrS7B5B9RVchXD6PlmH5sBfuq4Kou#fidkdWxOYHwnPyd1blpxYHZxWjA0VDxEQnZXcndCbGMzUXNsRE1PNVxdVDczc39sZFQwZ3JnY0NHfENhamlfQEZWNVY9bVdrdjM9QWFdU0F9NkF1fHx2U0hEb0JSQUpBSnJuQV9iSW5CbjdXNTVzbXBsNlNqcCcpJ2N3amhWYHdzYHcnP3F3cGApJ2lkfGpwcVF8dWAnPyd2bGtiaWBabHFgaCcpJ2BrZGdpYFVpZGZgbWppYWB3dic%3FcXdwYHgd', 'cs_test_a2gb3y8A2jIUfj3rWUr6hIHtDUkKy78Ab48NHhDBZ8J5010f1GOKbwjTsT')
