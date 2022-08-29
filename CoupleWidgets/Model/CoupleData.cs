using Newtonsoft.Json.Linq;

namespace CoupleWidgets.Model
{
    public class CoupleData
    {
        public string firstCoupleName { get; set; }
        public string secondCoupleName { get; set; }
        public string firstCoupleImage { get; set; }
        public string secondCoupleImage { get; set; }
        public double windowPositionX { get; set; }
        public double windowPositionY { get; set; }
        public bool visibility { get; set; }

        public CoupleData(string json=null)
        {
            if (json != null)
            {
                var jObject = JObject.Parse(json);

                firstCoupleName = (string)jObject["firstCoupleName"];
                secondCoupleName = (string)jObject["secondCoupleName"];
                firstCoupleImage = (string)jObject["firstCoupleImage"];
                secondCoupleImage = (string)jObject["secondCoupleImage"];
                windowPositionX = (double)jObject["windowPositionX"];
                windowPositionY = (double)jObject["windowPositionY"];
                visibility = (bool)jObject["visibility"];
            }
        }

    }
}
