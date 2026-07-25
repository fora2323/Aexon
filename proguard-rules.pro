-repackageclasses ''            
-flattenpackagehierarchy ''   
-allowaccessmodification         
-ignorewarnings                  
-dontnote                        
-dontwarn **                     
-optimizations !code/simplification/arithmetic 
-optimizationpasses 5           
-overloadaggressively            

-keep class com.aexon.starter.server.AexonServer { *; }
-keep class com.aexon.starter.server.AexonServer$* { *; }
-keepclassmembers class com.aexon.starter.server.AexonServer {
    public static void main(java.lang.String[]);
}

-keep class rikka.shizuku.ShizukuProvider { *; }        
-keep class rikka.shizuku.ShizukuProvider$* { *; }     
-keep class moe.shizuku.api.BinderContainer { *; }      
-keep class moe.shizuku.api.BinderContainer$* { *; }