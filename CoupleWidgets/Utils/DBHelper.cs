using CoupleWidgets.Model;
using Newtonsoft.Json.Linq;
using System;
using System.IO;

namespace CoupleWidgets.Utils
{
    class DBHelper
    {

        public CoupleData coupleData { get; set; }

        public DBHelper()
        {
            readData();
        }

        // Ceate file if not exist
        private string getFilePath()
        {
            string baseDirectoryPath = AppDomain.CurrentDomain.BaseDirectory;
            string filePath = baseDirectoryPath + "data.json";

            if (!File.Exists(filePath))
            {
                File.Create(filePath).Dispose();

                var data = new CoupleData()
                {
                    firstCoupleName = "Name",
                    secondCoupleName = "Name",
                    firstCoupleImage = "",
                    secondCoupleImage = "",
                    windowPositionX = 0.0,
                    windowPositionY = 0.0,
                    visibility = false
                };

                writeFile(filePath, data);
            }
            
            return filePath;
        }

        // Read all data
        public void readData()
        {
            try
            {
                string jsonFile = getFilePath();
                var json = File.ReadAllText(jsonFile);

                coupleData = new CoupleData(json);
            }
            catch (Exception)
            {
                throw;
            }

        }

        // Write file
        private void writeFile(string jsonFile, object data)
        {
            try
            {
                string output = Newtonsoft.Json.JsonConvert.SerializeObject(data, Newtonsoft.Json.Formatting.Indented);
                File.WriteAllText(jsonFile, output);
            }
            catch (Exception ex)
            {
                throw;
            }
        }


        public void updateFirstName(string name)
        {
            readData();
            coupleData.firstCoupleName = name;
            writeFile(getFilePath(), coupleData);
        }

        public void updateSecondName(string name)
        {
            readData();
            coupleData.secondCoupleName = name;
            writeFile(getFilePath(), coupleData);
        }

        public void updateFirstImage(string imagePath)
        {
            readData();
            coupleData.firstCoupleImage = copyFileToApplicationDirectory(imagePath);
            writeFile(getFilePath(), coupleData);
        }

        public void updateSecondImage(string imagePath)
        {
            readData();
            coupleData.secondCoupleImage = copyFileToApplicationDirectory(imagePath);
            writeFile(getFilePath(), coupleData);
        }

        public void updateVisivility(bool value)
        {
            readData();
            coupleData.visibility = value;
            writeFile(getFilePath(), coupleData);
        }

        public void updateWidgetPosition(double x, double y)
        {
            readData();
            coupleData.windowPositionX = x;
            coupleData.windowPositionY = y;
            writeFile(getFilePath(), coupleData);
        }


        private string copyFileToApplicationDirectory(string sourceFilePath)
        {
            string destFilePath = Path.Combine(
                AppDomain.CurrentDomain.BaseDirectory, 
                Path.GetFileName(sourceFilePath));

            File.Copy(sourceFilePath, destFilePath, true);

            return destFilePath;
        }


    }
}
