
importPackage(java.lang);

System.out.println(java.lang.String.format("ENGSCOP=[%s]",ENGSCOP));


var wNbMinutes = Math.floor((Math.random() * 30) + 10);

System.out.println(java.lang.String.format("NbMinutes=[%s]",wNbMinutes));

var wSleepDuration = wNbMinutes * 60 * 1000;

System.out.println(java.lang.String.format("SleepDuration=[%s]",new Double(wSleepDuration).intValue()));


System.out.println("before sleep");

Thread.sleep(new Double(2000).intValue());

System.out.println("after sleep");
