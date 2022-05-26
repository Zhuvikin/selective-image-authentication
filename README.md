**Selective  Image Authentication**

Build the application

````
gradle clean shadowJar -x test 
````

Run the application
````
java -jar application/build/libs/selective-authentication.jar [flags...]
````

The list of the supported flags:

`-e (--error-correction-rate) [arg]` - Error correction code rate (Default is 0.5)

`-s (--sigma) [arg]`                 - Sigma (Default is 10.0)

`-g (--gamma) [arg]`                 - Gamma (Default is 3.0)

`-d (--delta) [arg]`                 - Delta (Default is 10.0)

`-f (--source-path) [arg]`           - Source image file path (Required)

`-o (--output-path) [arg]`           - Output image file path. Default path is the same as the input one but with 'wm' suffix

`-a (--authentication-mode)`         - Authentication mode (Either authentication or watermarking mode should be specified)

`-w (--watermarking-mode)`           - Watermarking mode (Either authentication or watermarking mode should be specified)

`-p (--passphrase) [arg]`            - Passphrase (Required)

`-n (--name) [arg]`                  - Name of the user (Default is empty)

`-m (--max-name-length) [arg]`       - The maximum user name length (Default is 32)

*Usage examples*

Watermarking
 ```
java -jar application/build/libs/selective-authentication.jar -f watermarking/src/test/resources/lena.jpg -w -n "Супер-Пупер Rock-Star" -p 12345678
  
```

Authentication
```
java -jar application/build/libs/selective-authentication.jar -f watermarking/src/test/resources/lena_wm.jpg -a -p 12345678
```