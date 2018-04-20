DROP TABLE IF EXISTS geo_msg;
CREATE TABLE geo_msg (
  username VARCHAR(30),
  week INT,
  mornplace VARCHAR(5000),
  noonplace VARCHAR(5000),
  afterplace VARCHAR(5000),
  nightplace VARCHAR(5000),
  primary key (username, week)
)DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS footprint;
CREATE TABLE footprint (
  no int AUTO_INCREMENT,
  username VARCHAR(30),
  longitude DOUBLE,
  latitude DOUBLE,
  time DATE,
  time_dur VARCHAR(20),
  week INT,
  PRIMARY KEY (no)
) DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS temselect;
CREATE TABLE temselect (
  no int auto_increment,
  username varchar(20),
  longitude double,
  latitude double,
  primary key (no)
) DEFAULT CHARSET=utf8;




CREATE TABLE #{tableName} (
  longitude DOUBLE,
  latitude DOUBLE,
  time DATE,
  time_dur VARCHAR(20),
  week INT
) DEFAULT CHARSET=utf8;


SHOW TABLES LIKE 'gold1';



SELECT count('latitude')
FROM gold1
WHERE time_dur='after' AND
      week=0;

insert into footprint values('gold1', 112.597499, 37.80344, '2017-07-21', 'after', 0);

select min(longitude) minLongitude,
  max(longitude) maxLongitude,
  min(latitude) minLatitude,
  max(latitude) maxLatitude
from footprint
where username=#{username} AND
      time_dur=#{timeDur} and
      week=#{week} and
      time between #{startDate} AND #{endDate};



DELETE FROM footprint WHERE 1;

select * from footprint where username='gold1';
