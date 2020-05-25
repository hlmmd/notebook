import jpype
import jaydebeapi


url = 'jdbc:oracle:thin:@ip:port/name'
user = ''
password = ''
dirver = 'oracle.jdbc.OracleDriver'
jarFile = '/path_to/ojdbc6.jar'

conn = jaydebeapi.connect(dirver, url, [user, password], jarFile)
curs = conn.cursor()

for SNO in SNOS:

    sqlstr = 'select pic from id_pic_all  where sno = \''+SNO +'\''
    #print(sqlstr)
    #exit()
    #curs = conn.cursor()
    curs.execute(sqlstr)
    result = curs.fetchall()
    if len(result)==0:
        print(SNO,'not found')
        continue
    file = open( 'jpg/'+ str(SNO)+'.jpg', "wb")
    if result[0][0].length()>0:
        file.write(result[0][0].getBytes(1, int( result[0][0].length())  ))

    file.close()
    print(SNO)

curs.close()
conn.close()