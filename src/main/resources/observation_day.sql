SELECT p.date, p.name, p.phen, t.temp
FROM
( SELECT sub1.name AS name, sub1.phenomenon AS phen, sub1.obsdate AS date
	FROM 
	(SELECT name, phenomenon, CAST("timestamp" AS date) AS obsdate, count(*) as cnnt
		FROM station, observation
		WHERE station.observationid=observation.observationid
			AND CAST("timestamp" AS time) between CAST('09:00:00' AS time) AND CAST('17:00:00' AS time)
		GROUP BY name, phenomenon, obsdate
	) AS sub1 
	GROUP BY sub1.name, sub1.phenomenon, sub1.obsdate, sub1.cnnt
	HAVING (sub1.name, sub1.obsdate, sub1.cnnt) IN
	( SELECT sub2.name, sub2.obsdate, max(cnt)
		FROM (
		SELECT name, phenomenon, CAST("timestamp" AS date) AS obsdate, count(*) AS cnt
			FROM observation, station
			WHERE station.observationid=observation.observationid
				AND CAST("timestamp" AS time) between CAST('09:00:00' AS time) AND CAST('17:00:00' AS time)
			GROUP BY name, phenomenon, obsdate
		) AS sub2
		GROUP BY sub2.name, sub2.obsdate
		HAVING sub1.obsdate=obsdate
		)
	) AS p,
( SELECT name AS name, round(avg(CAST(airtemperature AS numeric)),2) AS temp, CAST("timestamp" AS date) AS date
		FROM station, observation
		WHERE station.observationid=observation.observationid
			AND CAST("timestamp" AS time) between CAST('09:00:00' AS time) AND CAST('17:00:00' AS time)
		GROUP BY name, date	
	) AS t
WHERE p.name=t.name AND p.date=t.date
;