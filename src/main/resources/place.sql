SELECT dp.fdate as fdate, dp.pname as pname, dp.pphen AS dayphen, dt.avg AS daymaxtemp, np.pphen AS nightphen, nt.avg AS nightmintemp
	FROM
	(SELECT sub1.date fdate, sub1.name pname, sub1.phenomenon pphen
		FROM (
		SELECT date, name, place.phenomenon, count(*) as cnnt
			FROM forecast, place
			WHERE place.forecastid = forecast.forecastid AND forecast.timeofday LIKE 'day'
			GROUP BY date, name, place.phenomenon
		) AS sub1
		GROUP BY sub1.date, sub1.name, pphen, sub1.cnnt
		HAVING	(sub1.date, sub1.name, sub1.cnnt) IN (
		SELECT sub2.date, sub2.name, max(cnt)
			FROM (
			SELECT f2.date, p2.name, p2.phenomenon AS pphen, count(*) AS cnt
				FROM forecast f2, place p2
				WHERE p2.forecastid = f2.forecastid AND f2.timeofday LIKE 'day'
				GROUP BY p2.name, f2.date, pphen
			) AS sub2
		GROUP BY date, name
		HAVING sub1.date=date AND sub1.name=name
		)
	) dp,
	( SELECT sub1.date fdate, sub1.name pname, sub1.phenomenon pphen
		FROM (
		SELECT date, name, place.phenomenon, count(*) as cnnt
			FROM forecast, place
			WHERE place.forecastid = forecast.forecastid AND forecast.timeofday LIKE 'night'
			GROUP BY date, name, place.phenomenon
		) AS sub1
	GROUP BY sub1.date, sub1.name, pphen, sub1.cnnt
	HAVING	(sub1.date, sub1.name, sub1.cnnt) IN (
		SELECT sub2.date, sub2.name, max(cnt)
			FROM (
			SELECT f2.date, p2.name, p2.phenomenon AS pphen, count(*) AS cnt
				FROM forecast f2, place p2
				WHERE p2.forecastid = f2.forecastid AND f2.timeofday LIKE 'night'
				GROUP BY p2.name, f2.date, pphen
			) AS sub2
		GROUP BY date, name
		HAVING sub1.date=date AND sub1.name=name
		)
	) np,
	( SELECT forecast.date AS fdate, place.name AS pname, avg(place.tempmax) AS avg
		FROM forecast, place
		WHERE place.forecastid = forecast.forecastid AND forecast.timeofday LIKE 'day'
		GROUP BY place.name, forecast.date
	) dt,
        ( SELECT forecast.date AS fdate, place.name AS pname, avg(place.tempmin) AS avg
		FROM forecast, place
		WHERE place.forecastid = forecast.forecastid AND forecast.timeofday LIKE 'night'
		GROUP BY place.name, forecast.date
	) nt
	WHERE dp.fdate = np.fdate AND dp.pname = np.pname AND dp.fdate = dt.fdate AND dp.pname = dt.pname AND dp.fdate = nt.fdate AND dp.pname=nt.pname
	GROUP BY dp.fdate, dp.pname, dayphen, daymaxtemp, nightphen, nightmintemp
	ORDER BY dp.fdate, dp.pname;