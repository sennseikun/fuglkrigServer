<?php
/*
*   This file is used on a webserver to get dynamic ip address for the game.
*/




function getUserIP()
{
    $client  = @$_SERVER['HTTP_CLIENT_IP'];
    $forward = @$_SERVER['HTTP_X_FORWARDED_FOR'];
    $remote  = $_SERVER['REMOTE_ADDR'];

    if(filter_var($client, FILTER_VALIDATE_IP))
    {
        $ip = $client;
    }
    elseif(filter_var($forward, FILTER_VALIDATE_IP))
    {
        $ip = $forward;
    }
    else
    {
        $ip = $remote;
    }

    return $ip;
}

if($_GET["code"]) {
    if ($_GET["code"] == "fuGlKrig03042017") {
        $user_ip = getUserIP();
        file_put_contents("ip.txt", $user_ip);
        
        echo("updated ip address");
    }
}
else {
    echo("didnt update");
}


?>