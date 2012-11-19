SELECT dp.fdate as fdate, np.phen AS nightphen, nt.tempmax AS nighttempmax, nt.tempmin AS nighttempmin, dp.phen AS dayphen, dt.tempmax AS daytempmax, dt.tempmin AS daytempmin
	FROM
	(SELECT sub1.date fdate, sub1.phenomenon phen
		FROM (
		SELECT date, phenomenon, count(*) as cnnt
			FROM forecast
			WHERE forecast.timeofday = 'day'
				AND date_part('day', forecast.date - forecast.timeofupdate) = :daysAfterToday
			GROUP BY date, phenomenon
		) AS sub1
		GROUP BY sub1.date, phen, sub1.cnnt
		HAVING	(sub1.date, sub1.cnnt) IN (
		SELECT sub2.date, max(cnt)
			FROM (
			SELECT date, phenomenon, count(*) AS cnt
				FROM forecast
				WHERE timeofday = 'day'
					AND date_part('day', forecast.date - forecast.timeofupdate) = :daysAfterToday
				GROUP BY date, phenomenon
			) AS sub2
		GROUP BY date
		HAVING sub1.date=date
		)
	) dp,
	(SELECT sub1.date fdate, sub1.phenomenon phen
		FROM (
		SELECT date, phenomenon, count(*) as cnnt
			FROM forecast
			WHERE forecast.timeofday = 'night'
				AND date_part('day', forecast.date - forecast.timeofupdate) = :daysAfterToday
			GROUP BY date, phenomenon
		) AS sub1
		GROUP BY sub1.date, phen, sub1.cnnt
		HAVING	(sub1.date, sub1.cnnt) IN (
		SELECT sub2.date, max(cnt)
			FROM (
			SELECT date, phenomenon, count(*) AS cnt
				FROM forecast
				WHERE timeofday = 'night'
					AND date_part('day', forecast.date - forecast.timeofupdate) = :daysAfterToday
				GROUP BY date, phenomenon
			) AS sub2
		GROUP BY date
		HAVING sub1.date=date
		)
	) np,
	( SELECT date, avg(tempmax) AS tempmax, avg(tempmin) AS tempmin
		FROM forecast
		WHERE timeofday = 'day'
			AND date_part('day', forecast.date - forecast.timeofupdate) = :daysAfterToday
		GROUP BY date
	) dt,
	( SELECT date, avg(tempmax) AS tempmax, avg(tempmin) AS tempmin
		FROM forecast
		WHERE timeofday = 'night'
			AND date_part('day', forecast.date - forecast.timeofupdate) = :daysAfterToday
		GROUP BY date
	) nt
	WHERE dp.fdate = np.fdate AND dt.date = dp.fdate AND nt.date = dp.fdate
	ORDER BY dp.fdate;