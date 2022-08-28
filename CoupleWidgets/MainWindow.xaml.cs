using CoupleWidgets.Utils;
using CoupleWidgets.Widgets;
using Microsoft.Win32;
using System;
using System.Reflection;
using System.Windows;
using System.Windows.Input;
using System.Windows.Media.Imaging;

namespace CoupleWidgets
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {

        private DBHelper helper;

        public MainWindow()
        {
            InitializeComponent();
        }

        void InstallMeOnStartUp()
        {
            try
            {
                RegistryKey rkApp = Registry.LocalMachine.OpenSubKey("SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run", true);
                rkApp.SetValue("CoupleWidgets", Assembly.GetExecutingAssembly().Location);
            }catch(Exception ex)
            {

            }
        }


        //From load event
        private void Window_Loaded(object sender, RoutedEventArgs e)
        {
            InstallMeOnStartUp();
            helper = new DBHelper();

            if (helper.show == true)
            {
                this.Hide();

                CoupleWidget coupleWidget = new CoupleWidget();
                coupleWidget.WindowStartupLocation = WindowStartupLocation.Manual;
                coupleWidget.Left = helper.positionX;
                coupleWidget.Top = helper.positionY;
                coupleWidget.Show();
            }


            FirstName.Text = helper.firstName;
            SecondName.Text = helper.secondName;

            if(helper.firstImage != "")
            {
                var bitmap = new BitmapImage();
                bitmap.BeginInit();
                bitmap.UriSource = new Uri(helper.firstImage);
                bitmap.EndInit();
                FirstImage.Source = bitmap;
            }

            if(helper.secondImage != "")
            {
                var bitmap = new BitmapImage();
                bitmap.BeginInit();
                bitmap.UriSource = new Uri(helper.secondImage);
                bitmap.EndInit();
                SecondImage.Source = bitmap;
            }
        }
        
        //Drag event
        private void DragMove(object sender, MouseButtonEventArgs e)
        {
            if (e.ButtonState == MouseButtonState.Pressed)
            {
                DragMove();
            }
        }

        //Close event
        private void CloseClick(object sender, RoutedEventArgs e)
        {
            this.Hide();
        }

        //First image event
        private void FirstImageClick(object sender, RoutedEventArgs e)
        {
            OpenFileDialog dialog = new OpenFileDialog();
            dialog.Filter = "Image files|*.bmp;*.jpg;*.jpeg;*.png";
            dialog.FilterIndex = 1;

            if (dialog.ShowDialog() == true)
            {
                var bitmap = new BitmapImage();
                bitmap.BeginInit();
                bitmap.UriSource = new Uri(dialog.FileName);
                bitmap.EndInit();
                FirstImage.Source = bitmap;

                helper.updateFirstImage(dialog.FileName.ToString());
            }
        }

        //Second image event
        private void SecondImageClick(object sender, RoutedEventArgs e)
        {
            OpenFileDialog dialog = new OpenFileDialog();
            dialog.Filter = "Image files|*.bmp;*.jpg;*.jpeg;*.png";
            dialog.FilterIndex = 1;

            if (dialog.ShowDialog() == true)
            {
                var bitmap = new BitmapImage();
                bitmap.BeginInit();
                bitmap.UriSource = new Uri(dialog.FileName);
                bitmap.EndInit();
                SecondImage.Source = bitmap;

                helper.updateSecondImage(dialog.FileName.ToString());
            }
        }


        private void ShowWidgetClick(object sender, RoutedEventArgs e)
        {
            helper.updateFirstName(FirstName.Text);
            helper.updateSecondName(SecondName.Text);
            helper.updateShow(true);

            CoupleWidget coupleWidget = new CoupleWidget();
            coupleWidget.WindowStartupLocation = WindowStartupLocation.Manual;
            coupleWidget.Left = helper.positionX;
            coupleWidget.Top = helper.positionY;
            coupleWidget.Show();
        }


    }
}
