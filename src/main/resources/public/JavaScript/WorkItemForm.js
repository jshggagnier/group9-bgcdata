
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
    console.log("Updating Table");
    var table = document.getElementById("InputTable");
    var StartDate = document.getElementById("startDate").valueAsDate;
    var EndDate = document.getElementById("endDate").valueAsDate;
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
        while (cellcounter != (weeks+1))
        {
            console.log(cellcounter + "/" + weeks);
            if (cellcounter < (weeks+1)) {
                currentcellpntr = currentrowpntr.insertCell(-1);
                if(RowCounter == 0){currentcellpntr.innerHTML = "Week " + cellcounter;}
                else
                {
                    if (cellcounter == 0) {currentcellpntr.innerHTML = "<input type='text' id='"+("cell"+RowCounter+":"+cellcounter)+"'>";}
                    else {currentcellpntr.innerHTML = "<input type='number' id='"+("cell"+RowCounter+":"+cellcounter)+"'>";}
                }
            }
            else if (cellcounter > (weeks+1)) {
                currentrowpntr.deleteCell(-1);
            }
            cellcounter = currentrowpntr.cells.length;
        }
    }
}

function createTeamString(){
    var table = document.getElementById("InputTable");
    var TableRows = (table.rows.length);
    var TableCells = document.getElementById("Row0").cells.length;
    var TableString = "";
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
    document.getElementById("TBstring").value = TableString;
}