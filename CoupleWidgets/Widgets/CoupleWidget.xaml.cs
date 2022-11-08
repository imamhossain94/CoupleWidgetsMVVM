using CoupleWidgets.Model;
using CoupleWidgets.Utils;
using System;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Threading;

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

            DispatcherTimer dispatcherTimer = new DispatcherTimer();
            dispatcherTimer.Tick += new EventHandler(dispatcherTimer_Tick);
            dispatcherTimer.Interval = new TimeSpan(0, 0, 1);
            dispatcherTimer.Start();
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
                MessageBox.Show(ex.Message);
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



        void DateDiff(DateTime dt1, DateTime dt2)
        {
            DateTime zeroTime = new DateTime(1, 1, 1);
            int leapDaysInBetween = CountLeapDays(dt1, dt2);
            TimeSpan span = dt2 - dt1;
            int years = (zeroTime + span).Year - 1;
            int months = (zeroTime + span).Month - 1;
            int days = (zeroTime + span).Day - (leapDaysInBetween % 2 == 1 ? 1 : 0);
            int weeks = days / 7;
            int remainingdays = days % 7;
            int hours = (zeroTime + span).Hour;
            int minutes = (zeroTime + span).Minute;
            int seconds = (zeroTime + span).Second;
            DayCount.Text = string.Format("{0}y {1}m {2}d\n{3}h {4}m {5}s", years, months, remainingdays, hours, minutes, seconds);
        }

        private static int CountLeapDays(DateTime dt1, DateTime dt2)
        {
            int leapDaysInBetween = 0;
            int year1 = dt1.Year, year2 = dt2.Year;
            DateTime dateValue;
            for (int i = year1; i <= year2; i++)
            {
                if (DateTime.TryParse("02/29/" + i.ToString(), out dateValue))
                {
                    if (dateValue >= dt1 && dateValue <= dt2)
                        leapDaysInBetween++;
                }
            }
            return leapDaysInBetween;
        }


        private void dispatcherTimer_Tick(object sender, EventArgs e)
        {
            DateTime parsedDate = DateTime.Parse(coupleData.startDate);
            DateTime currentDate = DateTime.Now;
            DateDiff(parsedDate, currentDate);
        }
    }
}
