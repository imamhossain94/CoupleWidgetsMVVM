using CoupleWidgets.Model;
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
        private CoupleData coupleData;

        public MainWindow()
        {
            InitializeComponent();
        }


        //From load event
        private void Window_Loaded(object sender, RoutedEventArgs e)
        {
            helper = new DBHelper();
            coupleData = helper.coupleData;

            if (coupleData.visibility == true)
            {
                Hide();
                // Create shortcut int startup folder
                // Extension.AddShortcutToStartupGroup(Constants.productName, Constants.publisherName);

                CoupleWidget coupleWidget = new CoupleWidget();
                coupleWidget.WindowStartupLocation = WindowStartupLocation.Manual;
                coupleWidget.Left = coupleData.windowPositionX;
                coupleWidget.Top = coupleData.windowPositionY;
                coupleWidget.Show();
            }

            FirstName.Text = coupleData.firstCoupleName;
            SecondName.Text = coupleData.secondCoupleName;

            if(coupleData.firstCoupleImage != "")
            {
                loadCoupleImage(coupleData.firstCoupleImage, FirstImage);
            }

            if(coupleData.secondCoupleImage != "")
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
            }catch(Exception ex)
            {
                throw;
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
            //this.Hide();
            Close();
        }

        //First image event
        private void FirstImageClick(object sender, RoutedEventArgs e)
        {
            OpenFileDialog dialog = new OpenFileDialog();
            dialog.Filter = "Image files|*.bmp;*.jpg;*.jpeg;*.png";
            dialog.FilterIndex = 1;

            if (dialog.ShowDialog() == true)
            {
                loadCoupleImage(dialog.FileName, FirstImage);
                helper.updateFirstImage(dialog.FileName);
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
                loadCoupleImage(dialog.FileName, SecondImage);
                helper.updateSecondImage(dialog.FileName.ToString());
            }
        }


        private void ShowWidgetClick(object sender, RoutedEventArgs e)
        {
            helper.updateFirstName(FirstName.Text);
            helper.updateSecondName(SecondName.Text);
            helper.updateVisivility(true);

            // Hide current window
            Hide();

            // Create shortcut int startup folder
            // Extension.AddShortcutToStartupGroup(Constants.productName, Constants.publisherName);

            // Show couple widget
            CoupleWidget coupleWidget = new CoupleWidget();
            coupleWidget.WindowStartupLocation = WindowStartupLocation.Manual;
            coupleWidget.Left = coupleData.windowPositionX;
            coupleWidget.Top = coupleData.windowPositionY;
            coupleWidget.Show();
        }


    }
}
