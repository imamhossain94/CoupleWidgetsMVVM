using Newtonsoft.Json.Linq;
using System;
using System.IO;

namespace CoupleWidgets.Utils
{
    class DBHelper
    {
        public string firstName { get; set; }
        public string secondName { get; set; }
        public string firstImage { get; set; }
        public string secondImage { get; set; }
        public double positionX { get; set; }
        public double positionY { get; set; }
        public bool show { get; set; }

        public DBHelper()
        {
            string jsonFile = "../../../CoupleWidgets/Data/data.json";
            var json = File.ReadAllText(jsonFile);
            try
            {
                var jObject = JObject.Parse(json);
                if (jObject != null)
                {
                    firstName = jObject["firstName"].ToString();
                    secondName = jObject["secondName"].ToString();
                    firstImage = jObject["firstImage"].ToString();
                    secondImage = jObject["secondImage"].ToString();
                    positionX = double.Parse(jObject["positionX"].ToString());
                    positionY = double.Parse(jObject["positionY"].ToString());
                    show = bool.Parse(jObject["show"].ToString());
                }
            }
            catch (Exception)
            {
                throw;
            }
        }

        public void updateFirstName(string name)
        {
            firstName = name;

            Console.WriteLine("--------------------");
            Console.WriteLine(firstName);
            Console.WriteLine("--------------------");


            string jsonFile = "../../../CoupleWidgets/Data/data.json";
            var json = File.ReadAllText(jsonFile);

            try
            {
                var jObject = JObject.Parse(json);
                jObject["firstName"] = firstName;
                string output = Newtonsoft.Json.JsonConvert.SerializeObject(jObject, Newtonsoft.Json.Formatting.Indented);
                File.WriteAllText(jsonFile, output);
            }
            catch (Exception ex)
            {
                throw;
            }
        }

        public void updateSecondName(string name)
        {
            secondName = name;
            string jsonFile = "../../../CoupleWidgets/Data/data.json";
            var json = File.ReadAllText(jsonFile);

            try
            {
                var jObject = JObject.Parse(json);
                jObject["secondName"] = secondName;
                string output = Newtonsoft.Json.JsonConvert.SerializeObject(jObject, Newtonsoft.Json.Formatting.Indented);
                File.WriteAllText(jsonFile, output);
            }
            catch (Exception ex)
            {
                throw;
            }
        }

        public void updateFirstImage(string imagePath)
        {
            firstImage = imagePath;
            string jsonFile = "../../../CoupleWidgets/Data/data.json";
            var json = File.ReadAllText(jsonFile);

            try
            {
                var jObject = JObject.Parse(json);
                jObject["firstImage"] = firstImage;
                string output = Newtonsoft.Json.JsonConvert.SerializeObject(jObject, Newtonsoft.Json.Formatting.Indented);
                File.WriteAllText(jsonFile, output);
            }
            catch (Exception ex)
            {
                throw;
            }
        }

        public void updateSecondImage(string imageString)
        {
            secondImage = imageString;
            string jsonFile = "../../../CoupleWidgets/Data/data.json";
            var json = File.ReadAllText(jsonFile);

            try
            {
                var jObject = JObject.Parse(json);
                jObject["secondImage"] = secondImage;
                string output = Newtonsoft.Json.JsonConvert.SerializeObject(jObject, Newtonsoft.Json.Formatting.Indented);
                File.WriteAllText(jsonFile, output);
            }
            catch (Exception ex)
            {
                throw;
            }
        }

        public void updateShow(bool value)
        {
            show = value;
            string jsonFile = "../../../CoupleWidgets/Data/data.json";
            var json = File.ReadAllText(jsonFile);

            try
            {
                var jObject = JObject.Parse(json);
                jObject["show"] = show;
                string output = Newtonsoft.Json.JsonConvert.SerializeObject(jObject, Newtonsoft.Json.Formatting.Indented);
                File.WriteAllText(jsonFile, output);
            }
            catch (Exception ex)
            {
                throw;
            }
        }

        public void updateWidgetPosition(double x, double y)
        {
            positionX = x;
            positionY = y;

            string jsonFile = "../../../CoupleWidgets/Data/data.json";
            var json = File.ReadAllText(jsonFile);
  
            try  
            {  
                var jObject = JObject.Parse(json);
                jObject["positionX"] = positionX;
                jObject["positionY"] = positionY;

                string output = Newtonsoft.Json.JsonConvert.SerializeObject(jObject, Newtonsoft.Json.Formatting.Indented);
                File.WriteAllText(jsonFile, output);
            }  
            catch (Exception ex)  
            {
                throw;
            }  
        }



    }
}
