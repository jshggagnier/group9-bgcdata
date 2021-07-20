function AddTeam(){
    var table = document.getElementById("InputTable");
    var TableRows = (table.rows.length-1);
    var newrow = table.insertRow(-1);
    newrow.id = "Row"+(TableRows+1);
    UpdateTable();
}

function DeleteTeam(){
    var table = document.getElementById("InputTable");
    var TableRows = (table.rows.length-1);
    if(TableRows == 1){alert("Must have at least one Team");}
    else
    {
        table.deleteRow(-1);
    }
}

function weeksBetween(StartDate, EndDate) {
    return Math.ceil((EndDate - StartDate) / (7 * 24 * 60 * 60 * 1000));//rounds up the amount of weeks between the two dates (the number is the amount of milliseconds in a week)
}

function UpdateTable(){
    //console.log("Updating Table");
    var table = document.getElementById("InputTable");
    var StartDate = document.getElementById("startDate").valueAsDate;
    var EndDate = document.getElementById("endDate").valueAsDate;
    if(EndDate < StartDate) 
    {
        document.getElementById("endDate").valueAsDate = StartDate;
    }
    var weeks = weeksBetween(StartDate,EndDate);
    if(weeks < 1) {weeks = 1;}
    var TableRows = (table.rows.length);
    var currentrowpntr;
    var cellcounter = 0;
    var currentcellpntr;
    for(var RowCounter = 0;RowCounter < TableRows;RowCounter++)
    {
        
        currentrowpntr = document.getElementById("Row"+RowCounter);
        cellcounter = currentrowpntr.cells.length;
        if(RowCounter == 0){
            var temp = cellcounter-1;
            while(temp > 0){currentrowpntr.deleteCell(temp);temp--}
            cellcounter = 1;
        }
        while (cellcounter != (weeks+1))
        {
            //console.log(cellcounter + "/" + weeks);
            if (cellcounter < (weeks+1)) {
                currentcellpntr = currentrowpntr.insertCell(-1);
                if(RowCounter == 0)
                {
                    var Cheese = new Date(StartDate.getTime() + ((cellcounter-1)*(7 * 24 * 60 * 60 * 1000) + (24 * 60 * 60 * 1000)));
                    //maybe concatenate calculation here
                    currentcellpntr.innerHTML = Cheese.toString().substring(0,15);
                    currentcellpntr.className = "itemheader";
                }
                else
                {
                    if (cellcounter == 0)
                    {
                        currentcellpntr.innerHTML = "<input type='text' id='"+("cell"+RowCounter+":"+cellcounter)+"' required><br><button type='button' class='delbtn' id='btn"+RowCounter+"' onclick='deleterow("+RowCounter+")'>Delete This Row</button>";
                        currentcellpntr.className = "rowname";
                    }
                    else {currentcellpntr.innerHTML = "<input type='number' id='"+("cell"+RowCounter+":"+cellcounter)+"'  min='0' required>";}
                }
            }
            else if (cellcounter > (weeks+1)) {
                currentrowpntr.deleteCell(-1);
            }
            cellcounter = currentrowpntr.cells.length;
        }
    }
    createTeamString();
}

function createTeamString(){
    var table = document.getElementById("InputTable");
    var TableRows = (table.rows.length);
    var TableCells = document.getElementById("Row0").cells.length;
    var TableString = "";
    //console.log(TableRows+"|I|"+TableCells);
    for(var RowCounter = 1;RowCounter < TableRows;RowCounter++)
    {
        TableString += "T"+RowCounter+":"
        for(var CellCounter = 0; CellCounter < TableCells; CellCounter++)
        {
            TableString += document.getElementById("cell"+RowCounter+":"+CellCounter).value;
            if(CellCounter != TableCells-1){
              TableString += ","
            }
        }
        TableString += "|";
    }
    //console.log(TableString);
    document.getElementById("TBstring").value = TableString;
}

function deleterow(row)
{
    console.log("deleterow"+row);
    var table = document.getElementById("InputTable");
    var StartDate = document.getElementById("startDate").valueAsDate;
    var EndDate = document.getElementById("endDate").valueAsDate;
    if(EndDate < StartDate) 
    {
        document.getElementById("endDate").valueAsDate = StartDate;
    }
    var weeks = weeksBetween(StartDate,EndDate);
    var TableRows = (table.rows.length-1);
    var target;
    var cellupdate = 0;
    var cellupdatepnt;
    if(TableRows == 1){alert("Must have at least one Team");}
    else
    {
        table.deleteRow(row);
        while (row < TableRows)
        {
            console.log(row+",");
            target = document.getElementById("Row"+(row+1));
            cellupdatepnt = document.getElementById("btn"+(row+1));
            cellupdatepnt.setAttribute("onclick", "deleterow("+row+");");
            console.log(cellupdatepnt.onclick);
            cellupdatepnt.id = "btn"+row;
            while(cellupdate < target.cells.length)
            {
                cellupdatepnt = document.getElementById("cell"+(row+1)+":"+cellupdate);
                cellupdatepnt.id = "cell"+row+":"+cellupdate;
                cellupdate++;
            }
            target.id = "Row"+row;
            row++;
            cellupdate = 0;
        }
    }
    createTeamString();
}

function FillTable(){
    var Teams = parseTeamString(document.getElementById("TBstring").value);
    UpdateTable();
    var i = 0;
    var TableCells = document.getElementById("Row0").cells.length;
    Teams.forEach(Row => {
        if(i != 0 && (Teams.length-1) != i){
          console.log(i);
          console.log(Teams);
          AddTeam();
        }
        for(var CellCounter = 0; CellCounter < TableCells; CellCounter++)
        {
            if(CellCounter == 0){
                Row[CellCounter] = Row[CellCounter].slice(3);
            }
            document.getElementById("cell"+(i+1)+":"+CellCounter).value = Row[CellCounter];
        }
        i++;
  });
  UpdateTable();
  SelectType();
    }
    
    function parseTeamString(team){
        var i = 0;
        team = team.split("|");
        while(i < team.length){
        team[i] = team[i].split(",");
        var Team = "";
        var c = 0;
        team[i].forEach(t => {
            if(c == 0){
                Team += t.slice(3)+" ";
                c++;
            }else{
            Team += t+" ";
            }
        });
        i++;
    }
    return team;
    }
  function SelectType(){
    var value = document.getElementById("itemtype").value;
    if(value=="Dev"){
      document.getElementById("dev").checked = true;
    }else{
      document.getElementById("qa").checked = true;
    }
  }
  FillTable();