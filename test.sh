#/bin/bash

frmaddr=""
frmport=""
frmdura=""
frmnumF=""
frmstart1=""
frmcca1=""
frmstart2=""
frmcca2=""
frmstart3=""
frmcca3=""
tcplogPid=""
probePid=""
logport=55555

#user=$(echo /etc/passwd | awk -F: '{print $1}')
#print $user
checkParameter(){

	#test value of field
	# 1. ip address
	count=`echo $frmaddr | grep '\.' -o | wc -l`
	if [ $count -ne 3 ]
	then 
		echo TCPtester : E : IP Address is not valid
		exit 0
	fi

	ip0=`echo $frmaddr | cut -d'.' -f1`
	if [ $ip0 -lt 0 ]
	then 
		echo TCPtester : E : IP Address is not valid
		exit 0
	elif [ $ip0 -ge 256 ]
	then 
		echo TCPtester : E : IP Address is not valid
		exit 0
	fi

	ip1=`echo $frmaddr | cut -d'.' -f2`
	if [ $ip1 -lt 0 ]
	then 
		echo TCPtester : E : IP Address is not valid
		exit 0
	elif [ $ip1 -ge 256 ]
	then 
		echo TCPtester : E : IP Address is not valid
		exit 0
	fi

	ip2=`echo $frmaddr | cut -d'.' -f3`
	if [ $ip2 -lt 0 ]
	then 
		echo TCPtester : E : IP Address is not valid
		exit 0
	elif [ $ip2 -ge 256 ]
	then 
		echo TCPtester : E : IP Address is not valid
		exit 0
	fi

	ip3=`echo $frmaddr | cut -d'.' -f4`
	if [ $ip3 -lt 0 ]
	then 
		echo TCPtester : E : IP Address is not valid
		exit 0
	elif [ $ip3 -ge 256 ]
	then 
		echo TCPtester : E : IP Address is not valid
		exit 0
	fi

	#2. port number
	if [ $frmport -le 0 ]
	then
		echo TCPtester : E : PORT number is invalid
		exit 0
	elif [ $frmport -ge 65538 ]
	then
		echo TCPtester : E : PORT number is invalid 
		exit 0
	fi
	
	##3. cca
	#available=$(sudo sysctl net.ipv4 | grep tcp_available_congestion_control)

	#if echo $available | grep -q -w $frmcca 
	#then
	#	echo TCPtester : D : Congestion control OK
	#else
	#	echo "TCPtester : E : Invalid congestion control algorithm - $frmca"
	#fi

}


GuiTesterCompile(){
	cd GuiTester/src
	sudo rm *.class 
	cd Graph && sudo rm *.class
	cd ..
	javac Main.java
	cd ../..
}

executeTCPlog(){
	echo "Start TCPlog log : $logport"
	interval=1
	sudo ./TCPlog/tcplog.py -o socket --quiet --log-port $logport --filter-dst-port $frmport --filter-dst-port 5005 --filter-dst-port 5006 --filter-dst-port 5007 --whitelisting  &
	tcplogPid=$!
	echo "tcplog : $tcplogPid"
	sudo modprobe -r tcp_probe
	sudo modprobe tcp_probe full=1 port=0
	sudo chmod 444 /proc/net/tcpprobe
#cat /proc/net/tcpprobe > /home/jjun/Desktop/result_probe.xls &
	probePid=$!

}


executeGuitester(){
	cd ./GuiTester/src
	java Main $frmdura $logport &
	cd ../..
}

executeIperf(){
	if [ $frmnumF -ge 1 ] 
	then 
		sleep $frmstart1
		frmdura=`expr $frmdura - $frmstart1`
		if [ $frmnumF -eq 1 ]
		then
			echo "Flow 1 : dst-$frmaddr | port-$frmport | duration-$frmdura | cca-$frmcca1"
			sudo iperf -c $frmaddr -p $frmport -t $frmdura -Z $frmcca1
		else
			echo "Flow 1 : dst-$frmaddr | port-$frmport | duration-$frmdura | cca-$frmcca1"
			sudo iperf -c $frmaddr -p $frmport -t $frmdura -Z $frmcca1 &
		fi
	fi

	
	frmport=`expr $frmport + 1`

	if [ $frmnumF -ge 2 ] 
	then
		sleep $frmstart2
		frmdura=`expr $frmdura - $frmstart2`
		if [ $frmnumF -eq 2 ]
		then

			echo "Flow 2 : dst-$frmaddr | port-$frmport | duration-$frmdura | cca-$frmcca1"
			sudo iperf -c $frmaddr -p $frmport -t $frmdura -Z $frmcca2
		else
			echo "Flow 2 : dst-$frmaddr | port-$frmport | duration-$frmdura | cca-$frmcca1"
			sudo iperf -c $frmaddr -p $frmport -t $frmdura -Z $frmcca2 &
		fi
	fi
		
	frmport=`expr $frmport + 1`

	if [ $frmnumF -ge 3 ] 
	then 
		frmdura=`expr $frmdura - $frmstart3`

		sleep $frmstart3
		if [ $frmnumF -eq 3 ]
		then
			echo "Flow 3 : dst-$frmaddr | port-$frmport | duration-$frmdura | cca-$frmcca3"
			sudo iperf -c $frmaddr -p $frmport -t $frmdura -Z $frmcca3
		else
			echo "Flow 3 : dst-$frmaddr | port-$frmport | duration-$frmdura | cca-$frmcca3"
			sudo iperf -c $frmaddr -p $frmport -t $frmdura -Z $frmcca3 &
		fi
	fi
}

exitAll(){
	tcplogPid=$(pgrep -f TCPlog)
	echo "pid : $tcplogPid"
	sudo kill -9 $tcplogPid

#sudo kill -9 $probePid

}

mkdir -p ~/Desktop/YJ_output/$1
sudo chmod 766 ~/Desktop/YJ_output
#sudo chown jjun ~/Desktop/YJ_output
sudo chmod 766 ~/Desktop/YJ_output/$1
#sudo chown jjun ~/Desktop/YJ_output/$1

frmdata=$(yad --title "TCPtester" --form --field "Address" --field "Port" --field "duration" --field "flow#" --text "Write your TCP test configuration" --button=OK:0 --button=Cancel:1 --buttons-layout=center)

resultCode=$?
echo $resultCode>&1

frmaddr=$(echo $frmdata | awk 'BEGIN {FS="|" } {print $1 }')
frmport=$(echo $frmdata | awk 'BEGIN {FS="|" } {print $2 }')
frmdura=$(echo $frmdata | awk 'BEGIN {FS="|" } {print $3 }')
frmnumF=$(echo $frmdata | awk 'BEGIN {FS="|" } {print $4 }')

echo $frmport
if [ $frmnumF -eq 1 ] 
then 
	frmdata=$(yad --title "2 flows set" --form --field "flow1 Start : " --field "flow1 CCA : " --button=OK:0 --button=Cancel:1 --buttons-layout=center)
	resultCode=$?
	frmstart1=$(echo $frmdata | awk 'BEGIN {FS="|" } {print $1 }')
	frmcca1=$(echo $frmdata | awk 'BEGIN {FS="|" } {print $2 }')

elif [ $frmnumF -eq 2 ] 
then 
	frmdata=$(yad --title "2 flows set" --form --field "flow1 Start : " --field "flow1 CCA : " --field "flow2 Start : " --field "flow2 CCA : " --button=OK:0 --button=Cancel:1 --buttons-layout=center)
	resultCode=$?
	frmstart1=$(echo $frmdata | awk 'BEGIN {FS="|" } {print $1 }')
	frmcca1=$(echo $frmdata | awk 'BEGIN {FS="|" } {print $2 }')
	frmstart2=$(echo $frmdata | awk 'BEGIN {FS="|" } {print $3 }')
	frmcca2=$(echo $frmdata | awk 'BEGIN {FS="|" } {print $4 }')

elif [ $frmnumF -eq 3 ] 
then 
	frmdata=$(yad --title "3 flows set" --form --field "flow1 Start : " --field "flow1 CCA : " --field "flow2 Start : " --field "flow2 CCA : " --field "flow3 Start : " --field "flow3 CCA : " --button=OK:0 --button=Cancel:1 --buttons-layout=center)
	resultCode=$?
	frmstart1=$(echo $frmdata | awk 'BEGIN {FS="|" } {print $1 }')
	frmcca1=$(echo $frmdata | awk 'BEGIN {FS="|" } {print $2 }')
	frmstart2=$(echo $frmdata | awk 'BEGIN {FS="|" } {print $3 }')
	frmcca2=$(echo $frmdata | awk 'BEGIN {FS="|" } {print $4 }')
	frmstart3=$(echo $frmdata | awk 'BEGIN {FS="|" } {print $5 }')
	frmcca3=$(echo $frmdata | awk 'BEGIN {FS="|" } {print $6 }')

else 
	echo "Invalid the number of flow" 
	exit 0
fi

if [ $resultCode -eq -1 ]
then 
	exit 1
fi


if [ $resultCode != 0 ]
then
	echo TCPtester termiated
	exit 0
else
#echo $frmaddr>&1	
#	echo $frmport>&1
#	echo $frmdura>&1	
#	echo $frmnumF>&1
#	echo $frmcca>&1
	
	checkParameter
	GuiTesterCompile
	executeGuitester
	executeTCPlog
	executeIperf
	exitAll

fi
