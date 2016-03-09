function submitValue(Label,dir,link){

  var submit =$('input[name="selection"]:checked').val();

  if (dir==1)
  {
    if (submit==null)
    {
      window.alert("pick a value");
      return;
    }
    else
    {
      document.cookie= Label + "=" + submit + ";";
      window.location.href = link;
      return;
    }
  }
  else{
      document.cookie= Label + "=" + submit + ";";
      window.history.back()
      return;
  }
  return;
}

function getCookie(cname) {
    var name = cname + "=";
    var ca = document.cookie.split(';');
    for(var i=0; i<ca.length; i++) {
        var c = ca[i].trim();
        if (c.indexOf(name) == 0) return c.substring(name.length,c.length);
    }
    return "";
}

function script(page) {
    var checked = getCookie(page);
    var list = document.forms[0].selection;
    var i;
    for (i = 0; i < list.length; i++) {
        if (list[i].value == checked) {
          list[i].checked = true;
          return;
        }
    }
}

function submitVas(Label,dir,link){


  var submit = document.getElementById("score").innerHTML;

  if (dir==1)
  {
    if (submit==null)
    {
      window.alert("pick a value");
      return;
    }
    else
    {
      document.cookie= Label + "=" + submit + ";";
      window.location.href = link;
      return;
    }
  }
  else{
      document.cookie= Label + "=" + submit + ";";
      window.history.back()
      return;
  }
  return;
  
}

function vasLoad(page) {
    var value = getCookie(page);

    if (value == null || value == 0)
    {
      return;
    }

    document.getElementById("score").innerHTML = value;

    var s = $('#awesome2').slider();
    s.slider('value',value);

    return;
}

function loadResult() {
    var list = ["Mobility","SelfCare","UsualActivities","Pain/Discomfort","Anxiety/Depression","VAS"];
    var i;
    var value;
    for (i = 0; i < list.length; i++) {
        value = getCookie(list[i]);

        document.getElementById(list[i]).innerHTML = value;
    }
}
