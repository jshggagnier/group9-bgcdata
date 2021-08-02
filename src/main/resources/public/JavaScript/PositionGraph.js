function weeksBetween(StartDate, EndDate) {
  return Math.ceil((EndDate - StartDate) / (7 * 24 * 60 * 60 * 1000));//rounds up the amount of weeks between the two dates (the number is the amount of milliseconds in a week)
}

function calculateGraph() {
  var currentdate = new Date();
  var firstDay = new Date(currentdate.getFullYear(), currentdate.getMonth() - 1, 1);
  var lastDay = new Date(currentdate.getFullYear(), currentdate.getMonth() + 4, 0);

  var formattedSeries = [];

  var FilledElem = {};
  var UnfilledElem = {};
  var linelength = weeksBetween(firstDay, lastDay);
  var FilledData = [];
  var UnfilledData = [];
  while (FilledData.length < linelength) { FilledData.push(0); }
  while (UnfilledData.length < linelength) { UnfilledData.push(0); }

  Positions.forEach(function (item, index) {
    var x = 0;
    //console.log(item,index);
    if (item.isFilled)// Affects both lines
    {
      while (x < linelength) {
        if (item.hasEndDate) {
          if ((new Date(item.startDate) < new Date(firstDay.getTime() + (x * 6.048e+8))) && (new Date(firstDay.getTime() + (x * 6.048e+8)) < new Date(item.endDate))) {
            FilledData[x] += 1;
            UnfilledData[x] += 1;
          }
        }
        else {
          if ((new Date(item.startDate) < new Date(firstDay.getTime() + (x * 6.048e+8)))) {
            FilledData[x] += 1;
            UnfilledData[x] += 1;
          }
        }
        x++;
      }
    }
    else //only Affects the unfilled graph
    {
      while (x < linelength) {
        var StartScaling = weeksBetween(new Date(item.startDate), new Date(firstDay.getTime() + (x * 6.048e+8)))
        if (item.hasEndDate) {
          if ((new Date(item.startDate) < new Date(firstDay.getTime() + (x * 6.048e+8))) && (new Date(firstDay.getTime() + (x * 6.048e+8)) < new Date(item.endDate))) {
            UnfilledData[x] += 1;
          }
        }
        else {
          if ((new Date(item.startDate) < new Date(firstDay.getTime() + (x * 6.048e+8)))) {
            UnfilledData[x] += 1;
          }
        }
        x++;
      }
    }
  })
  console.log(FilledData, UnfilledData);
  FilledElem.type = "line";
  FilledElem.name = "Filled Positions";
  FilledElem.pointStart = Date.UTC(firstDay.getUTCFullYear(), firstDay.getUTCMonth(), firstDay.getUTCDate());
  FilledElem.pointInterval = 6.048e+8;
  FilledElem.data = FilledData;

  UnfilledElem.type = "line";
  UnfilledElem.name = "Unfilled Positions";
  UnfilledElem.pointStart = Date.UTC(firstDay.getUTCFullYear(), firstDay.getUTCMonth(), firstDay.getUTCDate());
  UnfilledElem.pointInterval = 6.048e+8;
  UnfilledElem.data = UnfilledData;

  formattedSeries.push(FilledElem);
  formattedSeries.push(UnfilledElem);
  console.log(formattedSeries);

  Highcharts.chart('container', {

    title: {
      text: 'Position Graph'
    },

    subtitle: {
      text: 'Dates are rounded to the previous monday'
    },

    xAxis: {
      // title: {
      //   text: "Months ( 1 - 12 )"
      // }
      type: 'datetime',
      startOnTick: true,
      endOnTick: true,
      // min: new Date('2021/01/01').getTime(),
      //
      // max: [[${maxDate}]],
      // max: new Date('2022/01/01').getTime(),
      //IT SHOWS WEEKLY!!!!!!!!!! EACH WEEK. IF YOU SAY MAX IS 13 JULY, IT WILL SHOW
      //WHOLE WEEK WHERE JULY 13 IS
      min: firstDay,
      max: lastDay,
      dateTimeLabelFormats: {
        week: 'Week Starting %b %e'
      },
      units: [
        [
          'week', [1]
        ],
        [
          'month', [1]
        ]
      ],
      tickInterval: 7 * 24 * 3600 * 1000
    },
    plotOptions: {
      column: {
        stacking: 'normal',
        dataLabels: {
          enabled: false
        }
      }
    },


    series: formattedSeries
  });
}