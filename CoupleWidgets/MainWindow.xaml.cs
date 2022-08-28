using Microsoft.Win32;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;

namespace CoupleWidgets
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        public MainWindow()
        {
            InitializeComponent();
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
                var bitmap = new BitmapImage();
                bitmap.BeginInit();
                bitmap.UriSource = new Uri(dialog.FileName);
                //bitmap.DecodePixelHeight = 200;
                bitmap.EndInit();

                
                FirstImage.Source = bitmap;
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
                //bitmap.DecodePixelHeight = 200;
                bitmap.EndInit();

                SecondImage.Source = bitmap;
            }
        }



    }
}
