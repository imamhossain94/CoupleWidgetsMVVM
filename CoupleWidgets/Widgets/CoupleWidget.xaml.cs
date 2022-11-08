using CoupleWidgets.Model;
using CoupleWidgets.Utils;
using System;
using System.Windows;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;

namespace CoupleWidgets.Widgets
{
    /// <summary>
    /// Interaction logic for CoupleWidget.xaml
    /// </summary>
    public partial class CoupleWidget : Window
    {

        private DBHelper helper;
        private CoupleData coupleData;

        public CoupleWidget()
        {
            InitializeComponent();
        }

        //Drag event
        private void DragMove(object sender, MouseButtonEventArgs e)
        {
            if (e.ButtonState == MouseButtonState.Pressed)
            {
                helper.updateWidgetPosition(Left, Top);

                DragMove();
            }
        }

        //From load event
        private void Window_Loaded(object sender, RoutedEventArgs e)
        {
            helper = new DBHelper();
            coupleData = helper.coupleData;

            FirstName.Text = coupleData.firstCoupleName;
            SecondName.Text = coupleData.secondCoupleName;

            DateTime parsedDate = DateTime.Parse(coupleData.startDate);
            DateTime currentDate = DateTime.Now;

            DayCount.Text = currentDate.Subtract(parsedDate).Days.ToString() + " days";

            if (coupleData.firstCoupleImage != "")
            {
                loadCoupleImage(coupleData.firstCoupleImage, FirstImage);
            }

            if (coupleData.secondCoupleImage != "")
            {
                loadCoupleImage(coupleData.secondCoupleImage, SecondImage);
            }

        }

        private void loadCoupleImage(string path, System.Windows.Controls.Image ImageView)
        {
            try
            {
                var bitmap = new BitmapImage();
                bitmap.BeginInit();
                bitmap.UriSource = new Uri(path);
                bitmap.EndInit();
                ImageView.Source = bitmap;
            }
            catch (Exception ex)
            {
                throw;
            }
        }

        void OpenMainWindow(object sender, RoutedEventArgs e)
        {
            helper.updateVisivility(false);
            Hide();
            MainWindow mainWindow = new MainWindow();
            mainWindow.Show();
        }


    }
}
