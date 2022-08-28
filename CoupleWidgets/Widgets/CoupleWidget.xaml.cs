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
            FirstName.Text = helper.firstName;
            SecondName.Text = helper.secondName;

            if (helper.firstImage != "")
            {
                var bitmap = new BitmapImage();
                bitmap.BeginInit();
                bitmap.UriSource = new Uri(helper.firstImage);
                bitmap.EndInit();
                FirstImage.Source = bitmap;
            }

            if (helper.secondImage != "")
            {
                var bitmap = new BitmapImage();
                bitmap.BeginInit();
                bitmap.UriSource = new Uri(helper.secondImage);
                bitmap.EndInit();
                SecondImage.Source = bitmap;
            }


        }

        void OpenMainWindow(object sender, RoutedEventArgs e)
        {
            helper.updateShow(false);
            this.Hide();
            MainWindow mainWindow = new MainWindow();
            mainWindow.Show();
        }


    }
}
