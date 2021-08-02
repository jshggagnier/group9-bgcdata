function weeksBetween(StartDate, EndDate) {
  return Math.ceil((EndDate - StartDate) / (7 * 24 * 60 * 60 * 1000));//rounds up the amount of weeks between the two dates (the number is the amount of milliseconds in a week)
}

function calculateGraph() {
  var currentdate = new Date();
  var firstDay = new Date(currentdate.getFullYear(), currentdate.getMonth() - 1, 1);
  var lastDay = new Date(currentdate.getFullYear(), currentdate.getMonth() + 3, 0);

  var formattedSeries = [];

  Workitems.forEach(function (item, index) {
    var WorkElement = {};
    var WorkData = [];
    var WorkString = item.teamsAssigned;
    //console.log(WorkString);

    var countTeams = (WorkString.match(/\|/g) || []).length;
    var elemStartDate = new Date(item.startDate);
    var elemEndDate = new Date(item.endDate);
    //console.log(elemStartDate,elemEndDate);

    var countWeeks = weeksBetween(elemStartDate, elemEndDate);

    var RoundedDay = elemStartDate.getDay(); // Get current day number, converting Sun. to 7
    if(RoundedDay == 0) {roundedDay = 7};
    //console.log(RoundedDay);
    if (RoundedDay !== 1)                         // Only manipulate the date if it isn't Mon.
      {
        elemStartDate.setHours(-24 * (RoundedDay - 1));
      }   // Set the hours to day number minus 1 multiplied by negative 24

    WorkString = WorkString.split('|').join(',').split(',')

    var TeamCounterInt = 0;
    var y = 0;
    var DataPair = [];
    while (TeamCounterInt < countTeams) {
      WorkString.shift();
      WorkString.shift();
      while (y < countWeeks) {
        if(TeamCounterInt == 0)
        {
          DataPair = [];
          DataPair.push(Date.UTC(elemStartDate.getUTCFullYear(), elemStartDate.getUTCMonth(), elemStartDate.getUTCDate() + (7 * y)));
          DataPair.push(parseInt(WorkString.shift()));
          WorkData.push(DataPair);
        }
        else
        {
          WorkData[y][1] += parseInt(WorkString.shift());
        }
        y++;
      }
      TeamCounterInt++;
      y = 0;
    }
    //console.log(WorkString,countTeams,countWeeks);

    WorkElement.name = item.itemName;
    WorkElement.data = WorkData;
    WorkElement.type = "column";
    formattedSeries.push(WorkElement);
    //console.log(item, index)
  })
  Positions.forEach(function (item, index) {
    

    
    //console.log(item, index)
  })


  console.log(formattedSeries);

  Highcharts.chart('container', {

    title: {
      text: 'WorkItem Graph'
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